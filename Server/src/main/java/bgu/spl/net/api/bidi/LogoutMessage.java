package bgu.spl.net.api.bidi;

public class LogoutMessage extends Message {
    public LogoutMessage() {
        super((short)3);
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        return true;
    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public String toString() {
        return "LogoutMessage{}";
    }
}
