package bgu.spl.net.api.bidi;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private String userName, userPassword;
    private int currentConnectionId;
    private boolean loggedIn;
    private ConcurrentLinkedQueue<User> followMeList;
    private ConcurrentLinkedQueue<User> iFollowList;
    private ConcurrentLinkedQueue<Message> notifications;
    private ConcurrentLinkedQueue<Message> publishedPosts;
    private ConcurrentLinkedQueue<Message> publishedPms;
    private long lastLoggedInAt, lastLoggedOutAt;

    public User(String userName, String userPassword, int connectionId) {

        this.userName = userName;
        this.userPassword = userPassword;
        this.currentConnectionId = connectionId;
        this.loggedIn = false;
        this.followMeList = new ConcurrentLinkedQueue<>();
        this.iFollowList = new ConcurrentLinkedQueue<>();
        this.notifications = new ConcurrentLinkedQueue<>();
        this.publishedPosts = new ConcurrentLinkedQueue<>();
        this.publishedPms = new ConcurrentLinkedQueue<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public int getCurrentConnectionId() {
        return currentConnectionId;
    }

    public void setCurrentConnectionId(int currentConnectionId) {
        this.currentConnectionId = currentConnectionId;
    }

    public short getNumOfFollowers() {
        return (short) this.followMeList.size();
    }

    public short getNumOfFollowing() {
        return (short) this.iFollowList.size();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public ConcurrentLinkedQueue<User> getFollowMeList() {
        return followMeList;
    }

    public ConcurrentLinkedQueue<User> getiFollowList() {
        return iFollowList;
    }

    public ConcurrentLinkedQueue<Message> getNotifications() {
        return notifications;
    }

    public void addToFollower(User follower) {
        this.followMeList.add(follower);
    }

    public boolean addToFollowing(User toFollow) {
        return this.iFollowList.add(toFollow);
    }

    public boolean removeFromFolloing(User toUnFollow) {
        return this.iFollowList.remove(toUnFollow);
    }

    public void addPost(Message message) {
        this.publishedPosts.add(message);
    }

    public void loginUser() {
        this.loggedIn = true;
    }

    public short getNumOfPublishedPost() {
        return (short) publishedPosts.size();
    }

    public void addPm(Message message) {
        this.publishedPms.add(message);
    }

    public short getNumOfPublishedPm() {
        return (short) publishedPms.size();
    }

    public int getNumOfPosts() {
        return getNumOfPublishedPost()+getNumOfPublishedPm();
    }

    public long getLastLoggedInAt() {
        return lastLoggedInAt;
    }

    public long getLastLoggedOutAt() {
        return lastLoggedOutAt;
    }

    public void setLoggedInAt() {
        this.lastLoggedInAt = System.currentTimeMillis();
    }

    public void logoutUser() {
        this.loggedIn = false;
    }

    public void setLoggedOutAt() {
        this.lastLoggedOutAt = System.currentTimeMillis();
    }

    public void removeFromFollowers(User userToRemove){
        followMeList.remove(userToRemove);
    }

    public void addToNotifictions(Message message){
            notifications.add(message);
    }
}