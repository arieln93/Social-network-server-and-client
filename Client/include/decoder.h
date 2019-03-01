//
// Created by ariel on 12/31/18.
//

#ifndef BOOST_ECHO_CLIENT_DECODER_H
#define BOOST_ECHO_CLIENT_DECODER_H
#include <string>
#include <iostream>
#include "connectionHandler.h"

using namespace std;
class decoder{
public:
    decoder(ConnectionHandler& ch);
    string decode();

private:
    ConnectionHandler& connectionHandler;
    short bytesToShort(char *bytesArr);
    string simpleMsg(string firstOpcode, short secondOpcode);
    string ACK();
    string ACKFollow();
    string ACKUserList();
    string ACKStat();
    string Notification();
    string Error();

};



#endif //BOOST_ECHO_CLIENT_DECODER_H
