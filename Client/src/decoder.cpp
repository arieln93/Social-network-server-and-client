//
// Created by ariel on 12/31/18.
//

#include <decoder.h>

#include "decoder.h"

decoder::decoder(ConnectionHandler &ch): connectionHandler(ch){

}

/**
 * this function get bytes from the server using the connection handler,
 * @return the string representation of the message.
 */
string decoder::decode() {
    char op[2];
    connectionHandler.getBytes(op,2);
    short opcode= bytesToShort(op);
    if (opcode== 9){
        return Notification();
    }
    else if (opcode==10){
        return ACK();
    }
    else if (opcode==11){
        return Error();
    }
    return "null";
}

short decoder::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

string decoder::simpleMsg(string firstOpcode,short secondOpcode) {
    string str = firstOpcode+" "+to_string(secondOpcode);
    return str;
}
string decoder::ACK() {
    char secondOpcode[2];
    connectionHandler.getBytes(secondOpcode,2);
    short op = bytesToShort(secondOpcode);
    if ((op==1) | (op==2) | (op==3) | (op==5) | (op==6)){
        return simpleMsg("ACK", op);
    }
    else if (op==4){
        return ACKFollow();
    }
    else if (op==7){
        return ACKUserList();
    }
    else if (op==8){
        return ACKStat();
    }
    return "Null";

}
string decoder::ACKFollow() {
    string result="ACK 4 ";
    char numOfUsers[2];
    connectionHandler.getBytes(numOfUsers,2);
    short num = bytesToShort(numOfUsers);
    result.append(to_string(num));
    for (int i=0; i<num;i++){
        string userName;
        connectionHandler.getFrameAscii(userName,'\0');
        result.append(" "+userName);
    }
    return result;
}

string decoder::ACKUserList() {
    string result="ACK 7 ";
    char numOfUsers[2];
    connectionHandler.getBytes(numOfUsers,2);
    short num = bytesToShort(numOfUsers);
    result.append(to_string(num));
    for (int i=0; i<num;i++){
        string userName;
        connectionHandler.getFrameAscii(userName,'\0');
        result.append(" "+userName);
    }
    return result;
}

string decoder::Notification() {
    string result= "NOTIFICATION";
    char type[1];
    connectionHandler.getBytes(type,1);
    if (type[0]==0){
        result.append(" PM ");
    }
    else {
        result.append(" Public ");
    }
    string postingUser="";
    connectionHandler.getFrameAscii(postingUser,'\0');
    result.append(postingUser+" ");
    string content="";
    connectionHandler.getFrameAscii(content,'\0');
    result.append(content);
    return result;

}

string decoder::Error() {
    char secondOpcode[2];
    connectionHandler.getBytes(secondOpcode,2);
    return simpleMsg("ERROR", bytesToShort(secondOpcode));
}

string decoder::ACKStat() {
    string result="ACK 8 ";
    char numOf[2];
    connectionHandler.getBytes(numOf,2);
    short numPosts = bytesToShort(numOf);
    result.append(to_string(numPosts)+ " ");
    (*numOf) = numOf[0];
    connectionHandler.getBytes(numOf,2);
    short numFollowers = bytesToShort(numOf);
    result.append(to_string(numFollowers)+ " ");
    (*numOf) = numOf[0];
    connectionHandler.getBytes(numOf,2);
    short numFollowing = bytesToShort(numOf);
    result.append(to_string(numFollowing)+ " ");
    return result;
}
