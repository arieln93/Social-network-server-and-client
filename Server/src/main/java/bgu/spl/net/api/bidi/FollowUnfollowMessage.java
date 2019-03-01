package bgu.spl.net.api.bidi;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FollowUnfollowMessage extends Message {
    private int numOfUsers=-1;
    private char follow = 'n';
    private ConcurrentLinkedQueue<String> userNameList = new ConcurrentLinkedQueue<>();
    int decodeState=0;
    int len=0;
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    byte[] shortNumOfUsers = new byte[1<<10];
    int state=0;

    public FollowUnfollowMessage(){
        super((short) 4);
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {

        if (follow=='n'){
            if (nextByte == 0){
                follow= '0';
            }
            else follow='1';
            return false;
        }
        if (numOfUsers==-1){
            if (state==0){
                shortNumOfUsers[0]=nextByte;
                state++;
                return false;
            }
            shortNumOfUsers[1]=nextByte;
            numOfUsers= ((int) bytesToShort(shortNumOfUsers));
            return false;
        }
        // still read user names
        if (nextByte!='\0'){
            if (len >= bytes.length) {
                    bytes = Arrays.copyOf(bytes, len * 2);
                }
                bytes[len++] = nextByte;
            }
        else { // finish to read username, insert him to list.
            userNameList.add(new String(bytes, 0, len, StandardCharsets.UTF_8));
            len = 0;
            bytes = new byte[1 << 10];
            numOfUsers--;
            if (numOfUsers==0){ // finished reading user names!
                numOfUsers= userNameList.size();
                return true;
            }
        }
        return false; // not a full message yet - still reading user names.

    }

    @Override
    public byte[] encode() {
        return null;
    }

    public char getFollow() {
        return follow;
    }

    public short getNumOfUsers() {
        return (short) numOfUsers;
    }

    public ConcurrentLinkedQueue<String> getUserNameList() {
        return userNameList;
    }

    public ConcurrentLinkedQueue<String> getUsersToFollow() {
        return userNameList;
    }

    @Override
    public String toString() {
        return "FollowUnfollowMessage{" +
                "numOfUsers=" + numOfUsers +
                ", follow=" + follow +
                ", userNameList=" + userNameList +
                ", usersToFollow=" + userNameList +
                ", decodeState=" + decodeState +
                ", len=" + len +
                ", state=" + state +
                '}';
    }
}
