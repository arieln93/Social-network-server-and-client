package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

public class PostMessage extends Message {
    private String content, sendingUser;
    private Vector<String> additionalUsers;
    int decodeState=0;
    int len=0,userNamelen=0;
    boolean readingUser=false;
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private byte[] userName = new byte[1 << 10]; //start with 1k


    public PostMessage() {
        super((short)5);
        additionalUsers = new Vector<>();
    }

    public Vector<String> getAdditionalUsers() {
        return additionalUsers;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        if (readingUser==true){
            if (nextByte!=' ' & nextByte!='\0'){
                userName[userNamelen++] = nextByte;
                bytes[len++] = nextByte;
                return false; // still reading username
            }
            String usr = new String(userName, 0, userNamelen, StandardCharsets.UTF_8);
            boolean flag = false;
            for (String str: additionalUsers){
                if (str.compareTo(usr)==0){
                    flag = true;
                }
            }
            if (flag==false){
                additionalUsers.add(usr);
            }
            userName = new byte[1 << 10];
            userNamelen=0;
            readingUser = false;
        }
        if (nextByte!='\0'){ // still reading the post
            if (len >= bytes.length) {
                bytes = Arrays.copyOf(bytes, len * 2);
            }
            if (nextByte=='@'){ //reference to a user
                readingUser=true;
            }
            bytes[len++] = nextByte;
            return false;
        }
        else { // finish to read post
            content = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return true;
        }
    }

    @Override
    public byte[] encode() {
        return null;
    }
    public String getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(String sendingUser) {
        this.sendingUser = sendingUser;
    }
    @Override
    public String toString() {
        return "PostMessage{" +
                "content='" + content + '\'' +
                ", additionalUsers=" + additionalUsers +
                ", decodeState=" + decodeState +
                ", len=" + len +
                ", userNamelen=" + userNamelen +
                ", readingUser=" + readingUser +
                '}';
    }
}
