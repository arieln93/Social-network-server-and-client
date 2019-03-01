package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {

    private boolean shouldTerminate = false;
    private UserDataBase userDataBase;
    private int connectionId;
    private ConnectionsImpl connections;
    private User currentUser;

    public BidiMessagingProtocolImpl(UserDataBase userDataBase) {
        this.userDataBase = userDataBase;
    }

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl) connections;
    }

    @Override
    public void process(T message) {
        if (message instanceof Message) {
            switch (((Message) message).getOpcode()) {
                case 1:
                    processRegister((RegisterMessage) message);
                    break;
                case 2:
                    processLogin((LoginMessage) message);
                    break;
                case 3:
                    processLogout((LogoutMessage) message);
                    break;
                case 4:
                    processFollow((FollowUnfollowMessage) message);
                    break;
                case 5:
                    processPost((PostMessage) message);
                    break;
                case 6:
                    processPm((PmMessage) message);
                    break;
                case 7:
                    processUserList((UserListMessage) message);
                    break;
                case 8:
                    processStat((StatMessage) message);
                    break;

            }
        }
    }


    /*
     check to see if the user trying to register already exists if not add him
     if a user exists with the sane name he wont be added and the instance will be deleted.
     */
    private void processRegister(RegisterMessage message) {
        User newUser = new User(message.getName(), message.getPassword(), connectionId);
        if (!userDataBase.userExistByName(newUser.getUserName())) {
            userDataBase.addUser(newUser);
            connections.send(connectionId, new AckMessage(message.getOpcode()));
        } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
    }


    private void processLogin(LoginMessage message) {
        if (userDataBase.userExistByName(message.getName())) {//makes sure there is a user with that name
            User optionalUser = userDataBase.getUserByName(message.getName());// set current user
            if (optionalUser.getUserPassword().equals(message.getPassword())) {// make sure the password is correct
                if (!optionalUser.isLoggedIn()) {
                    currentUser = optionalUser;
                    currentUser.setCurrentConnectionId(connectionId);
                    currentUser.loginUser();
                    currentUser.setLoggedInAt();
                    connections.send(currentUser.getCurrentConnectionId(), new AckMessage(message.getOpcode()));
                    //send to the user all the messages he missed wile being logged out
                    for (Message msg : currentUser.getNotifications()) {
                        if (msg.getTimeStamp() > currentUser.getLastLoggedOutAt() & msg.getTimeStamp() < currentUser.getLastLoggedInAt()) {
                            if (msg instanceof PostMessage)
                                connections.send(connectionId, new NotificationMessage((char) 1, ((PostMessage) msg).getSendingUser(), ((PostMessage) msg).getContent()));
                            else if (msg instanceof PmMessage)
                                connections.send(connectionId, new NotificationMessage((char) 0, ((PmMessage) msg).getSendingUser(), ((PmMessage) msg).getContent()));
                        }
                    }
                } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
            } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
        } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
    }

    /*
    logging out the user, deleting his current connection id and setting his logout time.
     */
    private void processLogout(LogoutMessage message) {
        if (currentUser != null) {
            if (currentUser.isLoggedIn()) {
                connections.send(connectionId, new AckMessage(message.getOpcode()));
                connections.disconnect(connectionId);
                currentUser.logoutUser();
                currentUser.setLoggedOutAt();
                userDataBase.removeUserByConnectionId(connectionId);
                shouldTerminate = true;
            } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
        } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
    }

    private void processFollow(FollowUnfollowMessage message) {
        int counter = 0;
        ConcurrentLinkedQueue<String> success = new ConcurrentLinkedQueue<>();
        if (currentUser != null) {
            if (currentUser.isLoggedIn()) {
                if (message.getFollow() == '0') {  //follow
                    for (String userNameToFollow : message.getUserNameList()) {//get a list of all the users to follow
                        if (userDataBase.getUserByName(userNameToFollow) != null) {//for each user check if exists
                            if (!currentUser.getiFollowList().contains(userDataBase.getUserByName(userNameToFollow))) {//check if the user isn't already following the other user
                                if (currentUser.addToFollowing(userDataBase.getUserByName(userNameToFollow))) {//add the other user to following list
                                    success.add(userNameToFollow);
                                    userDataBase.getUserByName(userNameToFollow).addToFollower(currentUser);//update the other user follower list
                                    counter++;
                                }
                            }
                        }
                    }
                } else if (message.getFollow() == '1') { //un-follow
                    for (String userNameToUnFollow : message.getUserNameList()) {//get a list of all the users to un follow
                        if (userDataBase.getUserByName(userNameToUnFollow) != null) {//for each user check if exists
                            if (currentUser.getiFollowList().contains(userDataBase.getUserByName(userNameToUnFollow))) {//makes sure the user is followed
                                if (currentUser.removeFromFolloing(userDataBase.getUserByName(userNameToUnFollow))) {//remove the other user from the following list
                                    success.add(userNameToUnFollow);
                                    userDataBase.getUserByName(userNameToUnFollow).removeFromFollowers(currentUser);//update the other user follower list
                                    counter++;
                                }
                            }
                        }
                    }
                }
            } else {
                connections.send(connectionId, new ErrorMessage(message.getOpcode()));
            }
        }
        if (counter != 0)
            connections.send(connectionId, new AckMessage(message.getOpcode(), (short) counter, qToString(success)));
        else {
            connections.send(connectionId, new ErrorMessage(message.getOpcode()));
        }
    }

    private void processPost(PostMessage message) {//post = 1
        if (currentUser != null) {
            if (currentUser.isLoggedIn()) {
                message.setSendingUser(currentUser.getUserName());
                for (User followingUser : currentUser.getFollowMeList()) {//send notification to all following users
                    if (followingUser.isLoggedIn())
                        connections.send(followingUser.getCurrentConnectionId(), new NotificationMessage((char) 1, currentUser.getUserName(), message.getContent()));
                    else followingUser.addToNotifictions(message);
                }
                for (String userName : message.getAdditionalUsers()) {
                    if (!currentUser.getFollowMeList().contains(userDataBase.getUserByName(userName)))//check if the user is not already following
                        if (userDataBase.getUserByName(userName).isLoggedIn())
                            connections.send(userDataBase.getUserByName(userName).getCurrentConnectionId(), new NotificationMessage((char) 1, currentUser.getUserName(), message.getContent()));// send msg to the specific users
                        else userDataBase.getUserByName(userName).addToNotifictions(message);
                }
                connections.send(connectionId, new AckMessage(message.getOpcode()));
                currentUser.addPost(message);
            } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
        } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
    }

    private void processPm(PmMessage message) {
        if (currentUser != null) {
            if (currentUser.isLoggedIn()) {
                message.setSendingUser(currentUser.getUserName());
                if (userDataBase.getUserByName(message.getUserName()) != null) {
                    if (userDataBase.getUserByName(message.getUserName()).isLoggedIn())
                        connections.send(userDataBase.getUserByName(message.getUserName()).getCurrentConnectionId(), new NotificationMessage((char) 0, currentUser.getUserName(), message.getContent()));
                    else userDataBase.getUserByName(message.getUserName()).addToNotifictions(message);
                    connections.send(connectionId, new AckMessage(message.getOpcode()));
                    currentUser.addPm(message);
                } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
            } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
        } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
    }

    private void processUserList(UserListMessage message) {
        if (currentUser != null) {
            if (currentUser.isLoggedIn()) {
                String[] stringArrayToReturn = new String[userDataBase.getHowManyUsers()];
                int counter = 0;
                for (User user : userDataBase.getUsersRegisteringOrder()) {//creates array of users names
                    stringArrayToReturn[counter++] = user.getUserName() + " ";
                }
                connections.send(connectionId, new AckMessage(message.getOpcode(), (short) stringArrayToReturn.length, stringArrayToReturn));
            } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
        } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));

    }

    private void processStat(StatMessage message) {
        if (currentUser != null) {
            if (currentUser.isLoggedIn()) {
                if (userDataBase.getUsersRegisteringOrder().contains(userDataBase.getUserByName(message.getUserName()))) {
                    User otherUser = userDataBase.getUserByName(message.getUserName());
                    connections.send(connectionId, new AckMessage(message.getOpcode(), new short[]{otherUser.getNumOfPublishedPost(), otherUser.getNumOfFollowers(), otherUser.getNumOfFollowing()}));
                } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
            } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));
        } else connections.send(connectionId, new ErrorMessage(message.getOpcode()));

    }


    private String[] qToString(ConcurrentLinkedQueue<String> q) {
        String[] stringToReturn = new String[q.size()];
        int index = 0;
        for (String s : q) {
            stringToReturn[index] = s + " ";
            index++;
        }
        return stringToReturn;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}