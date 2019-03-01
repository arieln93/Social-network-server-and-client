package bgu.spl.net.api.bidi;


import java.util.Arrays;

public class ErrorMessage extends Message {
    private short messageOpcode;

    public ErrorMessage(short messageOpcode) {
        super((short) 11);
        this.messageOpcode = messageOpcode;
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        return false;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }

    public byte[] encode(){
        byte[] opcodeByte = shortToBytes(getOpcode());
        byte[] messageOpcodeByte = shortToBytes(messageOpcode);
        byte[] toReturn = concatAll(opcodeByte,messageOpcodeByte);
        return toReturn;
    }


    public  byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "messageOpcode=" + messageOpcode +
                '}';
    }
}
