#ifndef NETWORK_MODEL
#define NETWORK_MODEL

#include <map>
#include "Model.cpp"
#include "Pipe.cpp"
#include <string>
#include <vector>
#include<iostream> 

using namespace std;

class NetworkModel : public Model
{

public:
    map<string, Model *> *children;
    map<string, Pipe *> *pipes;
    int numberOfTicks;

    NetworkModel(Model *inputModel, Model *outputModel, int numTicks)
    {
        numberOfInputs = inputModel->numberOfInputs;
        inPorts = new Port*[numberOfInputs];
        for (int i = 0; i < numberOfInputs; i++)
        {
            inPorts[i] = inputModel->inPorts[i];
        }
        outPort = outputModel->outPort;
        numberOfTicks = numTicks;
        pipes = new map<string, Pipe*>();
        children = new map<string, Model*>();
    }

    void tick(int *netIn)
    {
        for (int tick = 0; tick < numberOfTicks; tick++)
        {

            lambda();

            for (int i = 0; i < numberOfInputs; i++)
            {
                inPorts[i]->currentValue = netIn[i];
            }

            executePipes();

            delta(netIn);
        }
    }

    int lambda()
    {
        map<string, Model *>::iterator itr;
        for (itr = children->begin(); itr != children->end(); itr++)
        {
            Model *currentModel = itr->second;
            int value = currentModel->lambda();
            currentModel->outPort->setValue(value);
            debugPrint("Lambda from " + itr->first + ": " + to_string(value));
        }
        return 0;
    }

    void delta(int *inputSet)
    {
        map<string, Model *>::iterator itr;
        for (itr = children->begin(); itr != children->end(); itr++)
        {
            Model * currentModel = itr->second;
            int * inputs = currentModel->convertInPortsToIntArr();
            currentModel->delta(inputs);
            delete inputs;
            if (verbose) {
                string state = "";
                for (int i=0; i< currentModel->stateSize; i++) {
                    state += to_string(currentModel->state[0]) + " ";
                }
                state.pop_back();
                printf("New state for %s : { %s }\n", itr->first.c_str(), state.c_str());
            }
        }
    }

    void executePipes()
    {
        map<string, Pipe *>::iterator itr;
        for (itr = pipes->begin(); itr != pipes->end(); itr++)
        {
            itr->second->passValue();
        }
    }

    void addModel(string modelName, Model *m)
    {
        children->insert(pair<string, Model*>(modelName, m));
    }

    void addPipe(string pipeName, Pipe * pipe) {
        pipes->insert(pair<string, Pipe*>(pipeName, pipe));
    }

    ~NetworkModel()
    {

        map<string, Pipe *>::iterator itr;
        for (itr = pipes->begin(); itr != pipes->end(); itr++)
        {
            delete itr->second;
        }
        delete pipes;
        
    }
};

#endif