package bgu.spl.net.api.bidi;

public abstract class Message {
    private short opcode;
    private long timeStamp;

    public Message(short opcode) {
        this.opcode = opcode;
        this.timeStamp = System.currentTimeMillis();
    }

    public abstract boolean decodeNextByte(byte nextByte);

    public abstract byte[] encode();

    public short getOpcode() {
        return opcode;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public abstract String toString();
}
