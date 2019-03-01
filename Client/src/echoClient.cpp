#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>
#include <customizer.h>
#include <decoder.h>
using namespace std;

/**
 * this class has a "start" function which is responsible
 * for reading input from the keyboard.
 */
class lineReader {
public:
    ConnectionHandler &connectionHandler;
    bool stopInput; // should indicates us if the user asked to logout.
    lineReader(ConnectionHandler &ch) : connectionHandler(ch), stopInput(false), shouldTerminate(false) {}

    void start() {
        customizer *cz = new customizer(connectionHandler);
        string input;
        // register
        while (shouldTerminate == false) {
            if (stopInput == false) {
                getline(cin, input);
                if (input == "LOGOUT") {
                    stopInput = true;
                }
                cz->customizeAndSend(input);
            }
        }
        delete cz;
    }

    void terminate() {
        this->shouldTerminate = true;
    }

    bool isTerminated() {
        return shouldTerminate;
    }

private:
    bool shouldTerminate;
};


int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    //start to send messages to the server:
    lineReader myLineReader(connectionHandler);
    thread task1(&lineReader::start, &myLineReader);

    //start to get messages from the server:
    decoder myDecocer(connectionHandler);
    bool stop = false;
    while (stop == false) {
        string line = myDecocer.decode();
        if (line != "null"){
            cout << line << endl;
        }
        if (line == "ACK 3") { // logging out, terminating the lineReader
            myLineReader.terminate();
            stop = true;
        }
        if (line == "ERROR 3") {
            myLineReader.stopInput=false;
        }
        if (myLineReader.isTerminated()) {
            stop = true;
        }
    }
    task1.join();
    return 0;
}
