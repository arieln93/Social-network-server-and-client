CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: EchoClient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/customizer.o bin/decoder.o $(LDFLAGS)

EchoClient: bin/connectionHandler.o bin/echoClient.o bin/customizer.o bin/decoder.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/customizer.o: src/customizer.cpp
	g++ $(CFLAGS) -o bin/customizer.o src/customizer.cpp

bin/decoder.o: src/decoder.cpp
	g++ $(CFLAGS) -o bin/decoder.o src/decoder.cpp

.PHONY: clean
clean:
	rm -f bin/*
