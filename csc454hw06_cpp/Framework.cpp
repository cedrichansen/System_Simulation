#ifndef FRAMEWORK
#define FRAMEWORK


#include <vector>
#include "Network.cpp"
#include <iterator>
#include "Trajectory.cpp"
#include <stdio.h>

using namespace std;
template <class IN, class OUT>
class Framework {
public:
    Network<IN, OUT> *network;
    vector <Trajectory*> trajectory;

    Time *currentTime;
    int maxNumberOfEvents;
    int numLoops;
    int networkEventsExecuted = 0;
    int maxOutputs;
    int outputsPrinted = 0;

    using eventPtr = Event<IN, OUT> *;
    typename vector<eventPtr>::iterator eventItr;
    typename vector<Trajectory*>::iterator trajItr;


    Framework(Network<IN, OUT> * n, vector <Trajectory*> traj) {
        network = n;
        trajectory = traj;
        currentTime = new Time(0, 0);
        maxNumberOfEvents = 999999;
        numLoops = 1;
        maxOutputs = 999999;
    }


    Framework(Network<IN, OUT> * n, vector <Trajectory*> traj, int maxNumberOfEvs, int numL, int maxOutput) {
        network = n;
        trajectory = traj;
        currentTime = new Time(0, 0);
        maxNumberOfEvents = maxNumberOfEvs;
        numLoops = numL;
        maxOutputs = maxOutput;
    }

    void start() {

        /** add all of the events which are read from the input trajectory*/
        for (trajItr = trajectory.begin(); trajItr < trajectory.end(); trajItr++) {
            for (int i = 0; i < network->numInputs; i++) {
                //tell each of the input ports/models about what it will be receiving
                Model<IN, OUT> * startingPoint = network->findConnectedModel(i);
                string startingModelName = network->getModelName(startingPoint);
                for (int j = 0; j < numLoops; j++) {
                    network->events->insert(
                            new Event<IN, OUT>(startingPoint, new Time(trajItr->time, 0), "external", startingModelName, trajItr->inputs[i]));
                }
            }
        }

        int eventsExecuted = 0;
        Event<IN, OUT> * nextEvent = network->events->remove();

        while (nextEvent->input.compare("-1") != 0 && eventsExecuted < maxNumberOfEvents && outputsPrinted < maxOutputs) {

            currentTime = nextEvent->time; //advance time
            executeEvent(nextEvent, currentTime); //execute event

            vector <eventPtr> generatedEvents = network->generateEvents(nextEvent);
            for (eventItr = generatedEvents.start(); eventItr< generatedEvents.end(); eventItr++) {
                network->events->insert(eventItr->second());
            }

            if (network->events->getNumberOfElements() > 0) {
                network->events = network->createConfluentEvent();
                nextEvent = network->events->remove();
            } else {
                nextEvent = NULL;
            }

            eventsExecuted++;
        }


        printf("\n\nSimulation complete");
    }

    void executeEvent(Event<IN, OUT> * e, Time * currentTime) {

        if (e->action.compare("internal") == 0 ) {

            string res = e->model->lambda();
            printf(e->time->toString() + " " + e->modelName + ": " + res + "\n");

            if (e->model->out == network->out) {
                if (numLoops != 1) {
                    if (networkEventsExecuted % numLoops == 0) {
                        printf("           " + e->time->toString() + " Network: " + res + "\n");
                        outputsPrinted++;
                    }
                } else {
                    printf("           " + e->time->toString() + " Network: " + res + "\n");
                }
                networkEventsExecuted++;
            }

            network->passPipeValues(); //pass values around
            e->model->internalTransition();

        } else if (e->action.compare("external") == 0) {

            e->model->externalTransition(currentTime, e->input);

        } else if (e->action.compare("confluent") == 0) {
            string res = e->model.lambda();
            printf(e->time->toString() + " " + e->modelName + ": " + res + "\n");
            if (e->model->out == network->out) {
                if (numLoops != 1) {
                    if (networkEventsExecuted % numLoops == 0) {
                        printf("           " + e->time->toString() + " Network: " + res + "\n");
                        outputsPrinted++;
                    }
                } else {
                    printf("           " + e->time->toString() + " Network: " + res + "\n");
                }
                networkEventsExecuted++;
            }
            network->passPipeValues(); //pass values around
            e->model->confluentTransition(currentTime, e->input);
        }

    }


};

#endif // FRAMEWORK


