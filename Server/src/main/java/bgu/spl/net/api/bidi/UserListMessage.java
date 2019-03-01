package bgu.spl.net.api.bidi;

public class UserListMessage extends Message {
    public UserListMessage() {
        super((short) 7);
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        return false;
    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public String toString() {
        return "UserListMessage{}";
    }
}