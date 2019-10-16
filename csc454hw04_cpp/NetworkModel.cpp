#ifndef NETWORK_MODEL
#define NETWORK_MODEL

#include <map>
#include "Model.cpp"
#include "Pipe.cpp"
#include <string>
#include <vector>

using namespace std;

class NetworkModel : public Model
{

public:
    map<string, Model *> children;
    vector<Pipe *> pipes;
    int numberOfTicks;

    NetworkModel(int *initial, Model *inputModel, Model *outputModel, int numTicks)
    {
        state = initial;
        children = map<string, Model *>();
        pipes = vector<Pipe *>();
        numberOfInputs = inputModel->numberOfInputs;
        inPorts = new Port[numberOfInputs];
        for (int i = 0; i < numberOfInputs; i++)
        {
            inPorts[i] = inputModel->inPorts[i];
        }
        outPort = outputModel->outPort;
        numberOfTicks = numTicks;
    }

    void tick(int *netIn)
    {
        for (int tick = 0; tick < numberOfTicks; tick++)
        {

            lambda();

            for (int i = 0; i < numberOfInputs; i++)
            {
                inPorts[i].currentValue = netIn[i];
            }

            executePipes();

            delta(netIn);
        }
    }

    int lambda()
    {
        map<string, Model *>::iterator itr;
        for (itr = children.begin(); itr != children.end(); itr++)
        {
            Model *currentModel = itr->second;
            currentModel->outPort.currentValue = currentModel->lambda();
            debugPrint("Lambda from " + itr->first + ": " + to_string(currentModel->lambda()));
        }
        return 0;
    }

    void delta(int *inputSet)
    {
        map<string, Model *>::iterator itr;
        for (itr = children.begin(); itr != children.end(); itr++)
        {
            Model * currentModel = itr->second;
            int * inputs = currentModel->convertInPortsToIntArr();
            currentModel->delta(inputs);
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
        for (int i = 0; i < pipes.size(); i++)
        {
            pipes[i]->passValue();
        }
    }

    void addModel(string modelName, Model *m)
    {
        children[modelName] = m;
    }

    ~NetworkModel()
    {
        delete inPorts;
    }
};

#endif