#ifndef FRAMEWORK
#define FRAMEWORK


#include <vector>
#include "Network.cpp"
#include <iterator>


using namespace std;
template <class IN, class OUT>
class Framework {
public:
    Network<IN, OUT> *network;
    vector <Trajectory> trajectory;

    Time *currentTime;
    int maxNumberOfEvents;
    int numLoops;
    int networkEventsExecuted = 0;
    int maxOutputs;
    int outputsPrinted = 0;


    Framework(Network<IN, OUT> * n, vector <Trajectory> traj) {
        network = n;
        trajectory = traj;
        currentTime = new Time(0, 0);
        maxNumberOfEvents = 999999;
        numLoops = 1;
        maxOutputs = 999999;
    }


    Framework(Network<IN, OUT> * n, vector <Trajectory> traj, int maxNumberOfEvs, int numL, int maxOutput) {
        network = n;
        trajectory = traj;
        currentTime = new Time(0, 0);
        maxNumberOfEvents = maxNumberOfEvs;
        numLoops = numL;
        maxOutputs = maxOutput;
    }

    void start() {

        /** add all of the events which are read from the input trajectory*/

        for (Trajectory t : trajectory) {
            for (int i = 0; i < t.inputs.length; i++) {
                //tell each of the input ports/models about what it will be receiving
                Model<IN, OUT> * startingPoint = network.findConnectedModel(i);
                string startingModelName = network.getModelName(startingPoint);
                for (int j = 0; j < numLoops; j++) {
                    network->events->insert(
                            new Event(startingPoint, new Time(t.time, 0), "external", startingModelName, t.inputs[i]));
                }
            }
        }

        int eventsExecuted = 0;
        Event nextEvent = network->events.remove();

        while (nextEvent.input.compare("-1") != 0 && eventsExecuted < maxNumberOfEvents && outputsPrinted < maxOutputs) {

            currentTime = nextEvent.time; //advance time
            executeEvent(nextEvent, currentTime); //execute event

            vector <Event<IN, OUT>*> generatedEvents = network.generateEvents(nextEvent);
            vector <Event<IN, OUT>*>::iterator itr;
            for (itr = generatedEvents->start(); itr< generatedEvents->end(); itr++) {
                network->events->insert(itr->second());
            }

            if (network->events->getNumberOfElements() > 0) {
                network->events = network.createConfluentEvent();
                nextEvent = network->events->remove();
            } else {
                nextEvent = Event(NULL, NULL, "nothing", "-1" , "-1");
            }

            eventsExecuted++;
        }


        printf("\n\nSimulation complete");
    }

    void executeEvent(Event e, Time currentTime) {

        if (e.action.equals("internal")) {

            String res = e.model.lambda();
            System.out.println(e.time.toString() + " " + e.modelName + ": " + res);

            if (e.model.out == this.network.out) {
                if (numLoops != 1) {
                    if (networkEventsExecuted % numLoops == 0) {
                        System.out.println("           " + e.time.toString() + " Network: " + res);
                        outputsPrinted++;
                    }
                } else {
                    System.out.println("           " + e.time.toString() + " Network: " + res);
                }
                networkEventsExecuted++;
            }

            network.passPipeValues(); //pass values around
            e.model.internalTransition();

        } else if (e.action.equals("external")) {

            e.model.externalTransition(currentTime, e.input);
            //network.passPipeValues(); //No need to pass values around.... Nothing was generated

        } else if (e.action.equals("confluent")) {
            String res = e.model.lambda();
            System.out.println(e.time.toString() + " " + e.modelName + ": " + res);
            if (e.model.out == this.network.out) {
                if (numLoops != 1) {
                    if (networkEventsExecuted % numLoops == 0) {
                        System.out.println("           " + e.time.toString() + " Network: " + res);
                        outputsPrinted++;
                    }
                } else {
                    System.out.println("           " + e.time.toString() + " Network: " + res);
                }
                networkEventsExecuted++;
            }
            network.passPipeValues(); //pass values around
            e.model.confluentTransition(currentTime, e.input);
        }

    }


};

#endif // FRAMEWORK


