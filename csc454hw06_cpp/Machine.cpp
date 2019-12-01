#ifndef PRESS
#define PRESS

#include "Port.cpp"
#include "Time.cpp"
#include "Model.cpp"
#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline

using namespace std;

class Machine : public Model<int, int> {

public: 
    int TIME_TO_PROCESS_PIECE;
    double timeRemainingOnPiece;

    Machine(Port<int> * inp, Port<int> * outp, int timeToProcessPiece, string name){
        numberOfPartsToProcess = 0;
        numberOfInputs = 1;
        in = new Port<int>*[numberOfInputs];
        in[0] = inp;
        out = outp;
        lastKnownTime = new Time(0,0);
        TIME_TO_PROCESS_PIECE = timeToProcessPiece;
    }

    string lambda() {
        out->currentValue = 1;
        return "finished one part!";
    }

    void externalTransition(Time * currentTime, string input) {

        if (numberOfPartsToProcess > 0) {
            //we received a piece, but we are already working on one.. decrement time appropriately
            Time elapsed(currentTime->realTime - lastKnownTime->realTime, 0);
            timeRemainingOnPiece -= elapsed.realTime;
        } else {
            //we are starting our first piece, set time to processing time...
            timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        }

        lastKnownTime->realTime = currentTime->realTime;
        lastKnownTime->discreteTime = currentTime->discreteTime;

        int partsAdded = in[0]->currentValue;
        in[0]->currentValue = 0;

        partsAdded = partsAdded == 0 ? stoi(input) : partsAdded; //if network input, input will come from in param rather than pipe
        numberOfPartsToProcess += partsAdded;
    }

    void internalTransition() {
        numberOfPartsToProcess--;
        lastKnownTime = new Time(lastKnownTime->realTime + timeRemainingOnPiece, 0);// we might care to know the time at which processing time was reset
        timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
    }

    void confluentTransition(Time * currentTime, string in) {
        internalTransition();
        lastKnownTime->realTime = currentTime->realTime;
        lastKnownTime->discreteTime = currentTime->discreteTime;
        externalTransition(currentTime, in);
    }

    Time * timeAdvance() {
        if (numberOfPartsToProcess == 0) {
            return new Time(DBL_MAX, 0);
        } else {
            return new Time(timeRemainingOnPiece, 0);
        }
    }

    double getMaxTimeAdvance() {
        return DBL_MAX;
    }

    std::string toString() {
        return name + "- Number of parts: " + to_string(numberOfPartsToProcess) + " Time remaining on current part: " + to_string(timeRemainingOnPiece);
    }


};

#endif