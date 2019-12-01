#ifndef DRILL
#define DRILL

#include "Port.cpp"
#include "Time.cpp"
#include "Model.cpp"

using namespace std;

template <class IN, class OUT> class Drill : public Model<int, int> {

public: 
    const static int TIME_TO_PROCESS_PIECE = 2;
    double timeRemainingOnPiece;

    Drill(Port<IN> * in, Port<OUT> out){
        numberOfPartsToProcess = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        this.out = out;
        lastKnownTime = new Time(0,0);
    }

    string lambda() {
        this.out->currentValue = 1;
        return "finished one part!";
    }

    void externalTransition(Time currentTime, string in) {

        if (numberOfPartsToProcess > 0) {
            //we received a piece, but we are already working on one.. decrement time appropriately
            Time elapsed(currentTime.realTime - lastKnownTime.realTime, 0);
            timeRemainingOnPiece -= elapsed.realTime;
        } else {
            //we are starting our first piece, set time to processing time...
            timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        }

        lastKnownTime = currentTime;

        int partsAdded = this.in[0]->currentValue;
        in[0]->currentValue = 0;

        partsAdded = partsAdded == 0 ? stoi(in) : partsAdded; //if network input, input will come from in param rather than pipe
        numberOfPartsToProcess += partsAdded;
    }

    void internalTransition() {
        numberOfPartsToProcess--;
        lastKnownTime = new Time(lastKnownTime.realTime + timeRemainingOnPiece, 0);// we might care to know the time at which processing time was reset
        timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
    }

    void confluentTransition(Time currentTime, string in) {
        internalTransition();
        lastKnownTime = currentTime; //this is to prevent the drill from thinking it is already done another part, since internal transition reset time
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
        return "Drill- Number of parts: " + numberOfPartsToProcess + " Time remaining on current part: " + timeRemainingOnPiece;
    }


};

#endif