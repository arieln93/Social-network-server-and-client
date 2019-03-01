package bgu.spl.net.api.bidi;


public class NotificationMessage extends Message {
    char notificationType;
    private String postingUser, content, toDecode;

    public NotificationMessage(char notificationType, String postingUser, String content) {
        super((short) 9);
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
        this.toDecode = getOpcode() + notificationType + postingUser + '\0' + content + '\0';
    }

    @Override
    public boolean decodeNextByte(byte nextByte) {
        return false; // not a full message yet - still reading user names.
    }

    public byte[] encode() {
        byte[] opcodeByte = shortToBytes(getOpcode());
        byte[] typeByte = new byte[1];
        if (notificationType == 0) {
            typeByte[0] = 0;
        } else {
            typeByte[0] = 1;
        }
        byte[] postingUserByte = (postingUser + '\0').getBytes();
        byte[] contentByte = (content + '\0').getBytes();
        byte[] b = mergeByteArray(opcodeByte, typeByte, postingUserByte, contentByte);
        return b;
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
        return "NotificationMessage{" +
                "notificationType=" + notificationType +
                ", postingUser='" + postingUser + '\'' +
                ", content='" + content + '\'' +
                ", toDecode='" + toDecode + '\'' +
                '}';
    }
}
