#ifndef MEMORY_MODEL
#define MEMORY_MODEL

#include "Model.cpp"
#include "Port.cpp"

class MemoryModel : public Model
{

public:
    MemoryModel(int *initialState, Port *in, Port *out)
    {
        state = new int [2];
        state[0] = initialState[0];
        state[1]= initialState[1];
        outPort = out;
        numberOfInputs = 1;
        inPorts = new Port *[numberOfInputs];
        inPorts[0] = in;
        stateSize = 2;
    }

    int lambda()
    {
        return state[0];
    }

    void delta(int *inputSet)
    {
        state[0] = state[1];
        state[1] = inputSet[0];
    }

    ~MemoryModel()
    {
        for (int i = 0; i < numberOfInputs; i++)
        {
            delete inPorts[i];
        }
        delete[] inPorts;
        delete state;
    }
};

#endif