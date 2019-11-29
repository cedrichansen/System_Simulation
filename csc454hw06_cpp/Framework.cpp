#ifndef FRAMEWORK
#define FRAMEWORK


#include <vector>
#include "Network.cpp"

class Framework {
public:
    Network *network;
    vector <Trajectory> trajectory;

    Time *currentTime;
    int maxNumberOfEvents;
    int numLoops;
    int networkEventsExecuted = 0;
    int maxOutputs;
    int outputsPrinted = 0;


    Framework(Network m, vector <Trajectory> traj) {
        this.network = m;
        this.trajectory = traj;
        currentTime = new Time(0, 0);
        this.maxNumberOfEvents = Integer.MAX_VALUE;
        this.numLoops = 1;
        this.maxOutputs = Integer.MAX_VALUE;
    }


    Framework(Network *m, vector <Trajectory> traj, int maxNumberOfEvents, int numLoops, int maxOutputs) {
        this.network = m;
        this.trajectory = traj;
        currentTime = new Time(0, 0);
        this.maxNumberOfEvents = maxNumberOfEvents;
        this.numLoops = numLoops;
        this.maxOutputs = maxOutputs;
    }

    void start() {

        /** add all of the events which are read from the input trajectory*/

        for (Trajectory t : trajectory) {
            for (int i = 0; i < t.inputs.length; i++) {
                //tell each of the input ports/models about what it will be receiving
                Model startingPoint = network.findConnectedModel(i);
                String startingModelName = network.getModelName(startingPoint);
                for (int j = 0; j < numLoops; j++) {
                    network.events.insert(
                            new Event(startingPoint, new Time(t.time, 0), "external", startingModelName, t.inputs[i]));
                }
            }
        }

        int eventsExecuted = 0;
        Event nextEvent = network.events.remove();

        while (nextEvent != null && eventsExecuted < this.maxNumberOfEvents && outputsPrinted < maxOutputs) {

            currentTime = nextEvent.time; //advance time
            executeEvent(nextEvent, currentTime); //execute event

            ArrayList <Event> generatedEvents = network.generateEvents(nextEvent);
            for (Event e : generatedEvents) {
                network.events.insert(e);
            }

            if (network.events.getNumberOfElements() > 0) {
                network.events = network.createConfluentEvent();
                nextEvent = network.events.remove();
            } else {
                nextEvent = null;
            }

            eventsExecuted++;
        }


        System.out.println("\n\nSimulation complete");
    }

public

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


