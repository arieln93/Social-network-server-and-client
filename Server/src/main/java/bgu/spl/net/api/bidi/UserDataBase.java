package bgu.spl.net.api.bidi;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class UserDataBase {
    private ConcurrentHashMap<String, User> userByName;
    private ConcurrentHashMap<Integer, User> userLoggedInByConnectionId;
    private ConcurrentLinkedQueue<User> usersRegisteringOrder;

    public UserDataBase() {
        this.userByName = new ConcurrentHashMap<>();
        this.userLoggedInByConnectionId = new ConcurrentHashMap<>();
        this.usersRegisteringOrder = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<User> getUsersRegisteringOrder() {
        return usersRegisteringOrder;
    }

    public int getHowManyUsers() {
        return usersRegisteringOrder.size();
    }

    public User getUserByName(String userName) {
        return userByName.get(userName);
    }

    public User getUserByConnectionId(int connectionID) {
        return userLoggedInByConnectionId.get(connectionID);
    }

    public boolean addUser(User user) {
        if (userByName.contains(user) | userLoggedInByConnectionId.contains(user))
            return false;
        else {
            userByName.put(user.getUserName(), user);
            userLoggedInByConnectionId.put(user.getCurrentConnectionId(), user);
            usersRegisteringOrder.add(user);
            return true;
        }
    }

    public boolean userExistByName(String userName) {
        return this.userByName.containsKey(userName);
    }

    public void removeUserByConnectionId (int connectionId){
        userLoggedInByConnectionId.remove(connectionId);
    }

}