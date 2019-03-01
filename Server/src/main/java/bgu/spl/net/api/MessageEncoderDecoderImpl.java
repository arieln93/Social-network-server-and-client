package bgu.spl.net.api;

import bgu.spl.net.api.bidi.*;

import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {


    int decodeState = 0;
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    short opcode;
    Message messageType;

    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (decodeState < 2) { // State 0-1: we still need to read the opcode
            //System.out.print((short) nextByte);
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
        if (decodeState == 3) {
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
                    decodeState += 3;
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
                    decodeState += 3;
                    break;
                case 8: // State
                    messageType = new StatMessage();
                    decodeState++;
                    break;
            }

        }
        if (decodeState >= 4) {
            if (decodeState == 4) {
                decodeState++;
                return null;
            }
            if (decodeState == 5) {
                if (messageType.decodeNextByte(nextByte)) {
                    decodeState = 6;
                }
            }
            if (decodeState == 6) {
                decodeState = 0;
                bytes = new byte[1 << 10]; //start with 1k
                len = 0;
                return messageType;
            }
        }
        return null;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }


    public static short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
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
    public byte[] encode(Message message) {
        byte[] toReturn = message.encode();
        return toReturn;
    }
}
