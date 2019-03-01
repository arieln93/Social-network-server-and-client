package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoderImpl;

import java.util.Arrays;


public class AckMessage extends Message {

    private short otherOpcode;
    private String[] strings;
    private short[] shorts;
    private short numOfUsers;
    private MessageEncoderDecoderImpl encodeDecode = new MessageEncoderDecoderImpl();


    public AckMessage(short otherOpcode, String... strings) {
        super((short) 10);
        this.otherOpcode = otherOpcode;
        this.strings = strings;
    }

    public AckMessage(short otherOpcode, short[] shorts) {
        super((short) 10);
        this.otherOpcode = otherOpcode;
        this.shorts = shorts;
    }
    public AckMessage (short otherOpcode,short numOfUsers,String... strings){
        super((short)10);
        this.otherOpcode = otherOpcode;
        this.strings = strings;
        this.numOfUsers=numOfUsers;
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        return false;
    }

    public byte[] encode() {
        switch (otherOpcode) {
            case 1: case 2: case 3: case 5: case 6: {
                byte[] opcodeByte = shortToBytes(getOpcode());
                byte[] otherOpcodeByte = shortToBytes(otherOpcode);
                byte[] stringsByte = encodeDecode.stringArrayToString(strings).getBytes();
                byte[] b = mergeByteArray(opcodeByte, otherOpcodeByte,stringsByte);
                encodeDecode.replaceSpaceWithZero(b);
                return b;
            }
            case 4:{
                byte[] opcodeByte = shortToBytes(getOpcode());
                byte[] otherOpcodeByte = shortToBytes(otherOpcode);
                byte[] numOfUsersByte = shortToBytes(numOfUsers);
                byte[] stringsByte = encodeDecode.stringArrayToString(strings).getBytes();
                byte[] b = mergeByteArray(opcodeByte, otherOpcodeByte,numOfUsersByte,stringsByte);
                encodeDecode.replaceSpaceWithZero(b);
                return b;
            }
            case 7: {
                byte[] opcodeByte = shortToBytes(getOpcode());
                byte[] otherOpcodeByte = shortToBytes(otherOpcode);
                byte[] numOfUsersByte = shortToBytes(numOfUsers);
                byte[] stringsByte = encodeDecode.stringArrayToString(strings).getBytes();
                byte[] b = mergeByteArray(opcodeByte, otherOpcodeByte,numOfUsersByte,stringsByte);
                encodeDecode.replaceSpaceWithZero(b);
                return b;
            }
            case 8:{
                byte[]b=mergeByteArray(shortToBytes(getOpcode()),
                        shortToBytes(otherOpcode),
                        shortToBytes(shorts[0]),
                        shortToBytes(shorts[1]),
                        shortToBytes(shorts[2]));
                encodeDecode.replaceSpaceWithZero(b);
                return b;
            }
        }return null;
    }

    public byte[] mergeByteArray(byte[]... byteArrays) {
        int len = 0;
        int index = 0;
        for (byte[] byteArray : byteArrays) {
            len = len + byteArray.length;
        }
        byte[] byteArrayToReturn = new byte[len];
        for (byte[] byteArray : byteArrays) {
            for (byte b : byteArray) {
                byteArrayToReturn[index++] = b;
            }

        }
        return byteArrayToReturn;
    }

    @Override
    public String toString() {
        return "AckMessage{" +
                "otherOpcode=" + otherOpcode +
                ", strings=" + Arrays.toString(strings) +
                ", shorts=" + Arrays.toString(shorts) +
                ", numOfUsers=" + numOfUsers +
                ", encodeDecode=" + encodeDecode +
                '}';
    }
}