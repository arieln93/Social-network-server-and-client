//
// Created by ariel on 12/31/18.
//

#include <customizer.h>
#include "customizer.h"

customizer::customizer(ConnectionHandler& connectionHandler) : connectionHandler(connectionHandler){

}

customizer::~customizer() {

}

/**
 * this function is responsible for encoding and sending the message to the server,
 * using the connection handler.
 * @param line
 */
bool customizer::customizeAndSend(string &line) {

    short opcode = getOpCode(line); // returns the opcode and cut the first word from the line
    if (opcode!=-1){
        if (opcode == 1)
            endoceAndSendRegister(line);
        else if (opcode == 2)
            endoceAndSendLogin(line);
        else if (opcode == 3)
            endoceAndSendLogout();
        else if (opcode == 4)
            endoceAndSendFollow(line);
        else if (opcode == 5)
            endoceAndSendPost(line);
        else if (opcode == 6)
            endoceAndSendPM(line);
        else if (opcode == 7)
            endoceAndSendUserlist();
        else if (opcode == 8)
            endoceAndSendStat(line);
    }
    return false;
}

// retrieves the opcode and cut the rest of the line.
short customizer::getOpCode(string &line) {
    string op;
    size_t space = line.find(" ");
    if (space == string::npos){
        op = line;
        line = "";
    }
    else{
        op = line.substr(0,space);
        line = line.substr(space+1);
    }

    if (op == "REGISTER")
        return 1;
    else if (op == "LOGIN")
        return 2;
    else if (op == "LOGOUT")
        return 3;
    else if (op == "FOLLOW")
        return 4;
    else if (op == "POST")
        return 5;
    else if (op == "PM")
        return 6;
    else if (op == "USERLIST")
        return 7;
    else if (op == "STAT")
        return 8;
    else
        return -1;
}


bool customizer::endoceAndSendRegister(string &line) {
    int numBytes = line.length() + 3;
    char bytesToSend[numBytes];
    shortToBytes((short)1,bytesToSend,0);
    replaceSpacesByZeroes(&bytesToSend[2], line);
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

bool customizer::endoceAndSendLogin(string &line) {
    int numBytes = line.length() + 3;
    char bytesToSend[numBytes];
    shortToBytes((short)2,bytesToSend,0);
    replaceSpacesByZeroes(&bytesToSend[2], line);
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

bool customizer::endoceAndSendLogout() {
    int numBytes = 2;
    char bytesToSend[numBytes];
    shortToBytes((short) 3,bytesToSend,0);
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

bool customizer::endoceAndSendFollow(string &line) {
    short numUsers;
    char follow = (getNextWord(line))[0];
    if (follow=='0'){
        follow=0;
    } else {
        follow=1;
    }
    numUsers = (short) stoi(getNextWord(line));
    int numBytes = line.length() + 6;
    char bytesToSend[numBytes];
    shortToBytes(4,bytesToSend,0);
    bytesToSend[2] = follow;
    shortToBytes(numUsers,bytesToSend,3);
    replaceSpacesByZeroes(&bytesToSend[5], line);
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

bool customizer::endoceAndSendPost(string &line) {
    int numBytes = line.length() + 3;
    char bytesToSend[numBytes];
    shortToBytes((short)5,bytesToSend,0);
    for (unsigned int i=0;i<line.length();i++){
        bytesToSend[i+2] = line[i];
    }
    bytesToSend[numBytes-1] = '\0';
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

bool customizer::endoceAndSendPM(string &line) {
    int numBytes = line.length() + 3;
    char bytesToSend[numBytes];
    shortToBytes((short)6,bytesToSend,0);
    bool flag= false;
    for (unsigned int i=0;i<line.length();i++){
        if ((line[i] == ' ') & (flag== false)){
            bytesToSend[i+2] = '\0';
            flag = true;
        }
        else
            bytesToSend[i+2] = line[i];
    }
    bytesToSend[numBytes-1] = '\0';
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

bool customizer::endoceAndSendUserlist() {
    int numBytes = 2;
    char bytesToSend[numBytes];
    shortToBytes((short) 7,bytesToSend,0);
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

bool customizer::endoceAndSendStat(string &line) {
    int numBytes = line.length()+3;
    char bytesToSend[numBytes];
    shortToBytes((short) 8,bytesToSend,0);
    for (unsigned int i=0;i<line.length();i++){
        bytesToSend[i+2] = line[i];
    }
    bytesToSend[numBytes-1] = '\0';
    return (connectionHandler.sendBytes(bytesToSend,numBytes));
}

void customizer::shortToBytes(short num, char *bytesArr,int off)
{
    bytesArr[off] = ((num >> 8) & 0xFF);
    bytesArr[off+1] = (num & 0xFF);
}
void customizer::replaceSpacesByZeroes(char *bytes, string &line){
    //cout<<"--customizer::replaceSpacesByZeroes: \n";

    for (unsigned int i=0;i<line.length();i++){
        if (line[i] == *(" "))
            *bytes = '\0';
        else
            *bytes = line[i];
        bytes++;
    }
    *bytes = '\0';
}
string customizer::getNextWord(string& line){
    string word;
    word = line.substr(0,line.find(" "));
    line = line.substr(line.find(" ")+1);
    return word;
}
