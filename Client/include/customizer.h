//
// Created by ariel on 12/31/18.
//

#ifndef BOOST_ECHO_CLIENT_CUSTOMIZER_H
#define BOOST_ECHO_CLIENT_CUSTOMIZER_H

#include <string>
#include <iostream>
#include "connectionHandler.h"

using namespace std;
class customizer{
public:
    customizer(ConnectionHandler &connectionHandler);
    ~customizer();
    bool customizeAndSend(string& line);
    ConnectionHandler &connectionHandler;
private:
    string getNextWord(string &str);
    void shortToBytes(short num, char* bytesArr, int off);
    void replaceSpacesByZeroes(char *bytes, string &line);
    short getOpCode(string& line);
    bool endoceAndSendRegister(string& line);
    bool endoceAndSendLogin(string& line);
    bool endoceAndSendLogout();
    bool endoceAndSendFollow(string& line);
    bool endoceAndSendPost(string& line);
    bool endoceAndSendPM(string& line);
    bool endoceAndSendUserlist();
    bool endoceAndSendStat(string& line);


};



#endif //BOOST_ECHO_CLIENT_CUSTOMIZER_H
