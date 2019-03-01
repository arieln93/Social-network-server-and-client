package bgu.spl.net.api.bidi;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RegisterMessage extends Message {
    private String name, password;
    int decodeState=0;
    int len=0;
    private byte[] bytes = new byte[1 << 10]; //start with 1k

    public RegisterMessage() {
        super((short) 1);
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        if (decodeState==0){ // still read username
            if (nextByte!='\0'){
                if (len >= bytes.length) {
                    bytes = Arrays.copyOf(bytes, len * 2);
                }
                bytes[len++] = nextByte;
            }
            else { // finish to read username
                name = new String(bytes, 0, len, StandardCharsets.UTF_8);
                len = 0;
                bytes = new byte[1 << 10];
                decodeState++;
            }
        }
        else { // still read password
            if (nextByte!='\0'){
                if (len >= bytes.length) {
                    bytes = Arrays.copyOf(bytes, len * 2);
                }
                bytes[len++] = nextByte;
            }
            else { // finish to read username
                password = new String(bytes, 0, len, StandardCharsets.UTF_8);
                return true; // we have a full message!
            }
        }

        return false; // not a full message yet - username or password is missing.
    }

    @Override
    public byte[] encode() {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "RegisterMessage{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", decodeState=" + decodeState +
                ", len=" + len +
                '}';
    }
}
