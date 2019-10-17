#ifndef XOR_MODEL
#define XOR_MODEL

#include "Model.cpp"
#include "Port.cpp"
#include <cstdio>   //for printf


class XORModel : public Model
{

public:
    XORModel(int *initial, Port *input1, Port *input2, Port *output)
    {
        state = new int [1];
        state[0] = initial[0];
        outPort = output;
        numberOfInputs = 2;
        inPorts = new Port*[numberOfInputs];
        inPorts[0] = input1;
        inPorts[1] = input2;
        stateSize = 1;
    }

    int lambda()
    {
        return state[0]; //corresponds to the statebit here
    }

    void delta(int *inputSet)
    {
        state[0] = inputSet[0] ^ inputSet[1];
    }

    ~XORModel()
    {
        for (int i = 0; i<numberOfInputs; i++) {
            delete inPorts[i];
        }
        delete[] inPorts;
        delete outPort;
        delete state;
    }
};

#endif