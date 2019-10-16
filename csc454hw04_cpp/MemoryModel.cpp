#ifndef MEMORY_MODEL
#define MEMORY_MODEL

#include "Model.cpp"
#include "Port.cpp"

class MemoryModel : public Model{


    public:

        MemoryModel(int * initialState, Port in, Port out) {
            state = initialState;
            outPort = out;
            numberOfInputs = 1;
            inPorts = new Port[numberOfInputs];
            inPorts[0] = in;
            stateSize = 2;
        }

        int lambda(){
            return state[0];
        }

        void delta(int * inputSet) {
            int x1 = state[1];
            int x2 = inputSet[0];
            int stateVals[2];
            stateVals[0] = x1;
            stateVals[1] = x2;
            state = stateVals;
        }

        ~MemoryModel(){
            delete inPorts;
        }


};


#endif