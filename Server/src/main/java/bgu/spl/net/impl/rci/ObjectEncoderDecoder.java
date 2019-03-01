package bgu.spl.net.impl.rci;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ObjectEncoderDecoder implements MessageEncoderDecoder<Serializable> {

    int decodeState = 0;
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    short opcode;
    Message messageType;

    @Override
    public Serializable decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (decodeState < 2) { // State 0-1: we still need to read the opcode
            System.out.print((short)nextByte);
            pushByte(nextByte);
            decodeState++;
        }
        // State 2: we have the opcode now.
        if (decodeState == 2) {
            // get the opcode
            byte[] op = Arrays.copyOfRange(bytes, 0, 2);
            opcode = bytesToShort(op);
            decodeState++;
        }
        // State 3: configuring which kind of message will it be.
        if (decodeState==3) {
            switch (opcode) {
                case 1: // Register
                    messageType = new RegisterMessage();
                    decodeState++;
                    break;
                case 2: // Login
                    messageType = new LoginMessage();
                    decodeState++;
                    break;
                case 3: // Logout
                    messageType = new LogoutMessage();
                    decodeState+=3;
                    break;
                case 4: // Follow Unfollow
                    messageType = new FollowUnfollowMessage();
                    decodeState++;
                    break;
                case 5: // Post
                    messageType = new PostMessage();
                    decodeState++;
                    break;
                case 6: // PM
                    messageType = new PmMessage();
                    decodeState++;
                    break;
                case 7: // UserList
                    messageType = new UserListMessage();
                    decodeState+=3;
                    break;
                case 8: // State
                    messageType = new StatMessage();
                    decodeState++;
                    break;
            }

        }
        if (decodeState >= 4) {
            if (decodeState==4) {
                System.out.print((char)nextByte);
                decodeState++;
                return null;
            }
            if (decodeState==5){
                System.out.print((char)nextByte);
                if (messageType.decodeNextByte(nextByte)) {
                    decodeState=6;
                }
            }
            if(decodeState==6){
                System.out.println("\n---------------------------------");
                decodeState = 0;
                bytes = new byte[1 << 10]; //start with 1k
                len = 0;
                System.out.println(messageType.toString());
                System.out.println("\n---------------------------------");
                return "";
            }
        }
        //System.out.println("Not a full message yet!");
        return null;
    }

    public byte[] encode(String message) {
        return (message + "\n").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    public static short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    public String stringArrayToString(String[] stringArray) {
        String stringToReturn = "";
        for (String string : stringArray) {
            stringToReturn = stringToReturn + string;
        }
        return stringToReturn;
    }

    public byte[] replaceSpaceWithZero(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] == ' ')
                byteArray[i] = '\0';
        }
        return byteArray;
    }



    @Override
    public byte[] encode(Serializable message) {
        return serializeObject(message);
    }

    private Serializable deserializeObject() {
        try {
            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(new byte[0]));
            return (Serializable) in.readObject();
        } catch (Exception ex) {
            throw new IllegalArgumentException("cannot desrialize object", ex);
        }

    }

    private byte[] serializeObject(Serializable message) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            //placeholder for the object size
            for (int i = 0; i < 4; i++) {
                bytes.write(0);
            }

            ObjectOutput out = new ObjectOutputStream(bytes);
            out.writeObject(message);
            out.flush();
            byte[] result = bytes.toByteArray();

            //now write the object size
            ByteBuffer.wrap(result).putInt(result.length - 4);
            return result;

        } catch (Exception ex) {
            throw new IllegalArgumentException("cannot serialize object", ex);
        }
    }

}
