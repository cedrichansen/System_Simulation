#ifndef MODEL
#define MODEL

#include <string>
#include "Port.cpp"

using namespace std;

class Model
{
public:
    bool verbose;
    int *state;
    int numberOfInputs;
    Port outPort;
    Port *inPorts;

    int stateSize;

    virtual int lambda() = 0;
    virtual void delta(int *inputSet) = 0;

    void debugPrint(string message)
    {
        if (verbose)
        {
            printf("%s\n", message.c_str());
        }
    }

    int *convertInPortsToIntArr()
    {
        int *vals = new int[numberOfInputs];
        for (int i = 0; i < numberOfInputs; i++)
        {
            vals[i] = inPorts[i].currentValue;
        }
        return vals;
    }
};
#endif