#ifndef MM
#define MM


#include "Model.cpp"
#include "Time.cpp"
#include "Port.cpp"

using namespace std;

class MemoryModel : public Model <int, int> {

public :

    int * state;

    MemoryModel(Port<int>* inp, Port<int> *  outp){
        state = new int [2];
        numberOfInputs = 1;
        in = new Port<int>*[numberOfInputs];
        in[0] = inp;
        out = outp;
        lastKnownTime = new Time(0,0);
    }
    string lambda() {
        out->currentValue = state[0];
        return to_string(out->currentValue);
    }

     void externalTransition(Time * elapsedTime, string inp) {
        int input = in[0]->currentValue;
        in[0]->currentValue = NULL;
        input = (input == NULL) ? stoi(inp) : input; //if network input, input will come from in param rather than pipe
        state[0] = state[1];
        state[1] = input;
    }

    void internalTransition() {
        out->currentValue = NULL;
    }

    void confluentTransition(Time * currentTime, string in) {
        internalTransition();
        lastKnownTime->realTime = currentTime->realTime;
        lastKnownTime->discreteTime = currentTime->discreteTime; //this is to prevent the press from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    Time * timeAdvance() {
        return new Time(1, 0);
    }

   double getMaxTimeAdvance() {
        return DBL_MAX;
    }

    string toString() {
        return "Memory Model - state : " + to_string(state[0]) + ", " + to_string(state[1]);
    }
};


#endif