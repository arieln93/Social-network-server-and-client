package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PmMessage extends Message {
    private String userName, content,sendingUser;
    int decodeState = 0;
    int len = 0;
    private byte[] bytes = new byte[1 << 10]; //start with 1k

    public PmMessage() {
        super((short) 6);
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        if (decodeState == 0) { // still read username
            if (nextByte != '\0') {
                if (len >= bytes.length) {
                    bytes = Arrays.copyOf(bytes, len * 2);
                }
                bytes[len++] = nextByte;
            } else { // finish to read username
                userName = new String(bytes, 0, len, StandardCharsets.UTF_8);
                len = 0;
                bytes = new byte[1 << 10];
                decodeState++;
            }
        } else { // still read content
            if (nextByte != '\0') {
                if (len >= bytes.length) {
                    bytes = Arrays.copyOf(bytes, len * 2);
                }
                bytes[len++] = nextByte;
            } else { // finish to read username
                content = new String(bytes, 0, len, StandardCharsets.UTF_8);
                return true; // we have a full message!
            }
        }

        return false; // not a full message yet - full username or content is still missing.
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
        return "PmMessage{" +
                "userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", decodeState=" + decodeState +
                ", len=" + len +
                '}';
    }
}
