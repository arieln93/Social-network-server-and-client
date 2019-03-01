package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StatMessage extends Message {
    private String userName;
    int decodeState = 0;
    int len = 0;
    private byte[] bytes = new byte[1 << 10];

    public StatMessage() {
        super((short) 8);
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        if (nextByte != '\0') {
            if (len >= bytes.length) {
                bytes = Arrays.copyOf(bytes, len * 2);
            }
            bytes[len++] = nextByte;
            return false; // not a full message yet.
        } else { // finish to read username
            userName = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return true;
        }
    }

    @Override
    public byte[] encode() {
        return null;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "StatMessage{" +
                "userName='" + userName + '\'' +
                ", decodeState=" + decodeState +
                ", len=" + len +
                '}';
    }
}

