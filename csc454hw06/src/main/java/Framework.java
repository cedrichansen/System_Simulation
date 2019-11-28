import java.util.ArrayList;

public class Framework {

    Network network;
    ArrayList<Trajectory> trajectory;

    Time currentTime;
    int maxNumberOfEvents;


    public Framework(Network m, ArrayList<Trajectory> trajectory) {
        this.network = m;
        this.trajectory = trajectory;
        currentTime = new Time(0,0);
        this.maxNumberOfEvents = Integer.MAX_VALUE;
    }



    public Framework(Network m, ArrayList<Trajectory> trajectory, int maxNumberOfEvents) {
        this.network = m;
        this.trajectory = trajectory;
        currentTime = new Time(0,0);
        this.maxNumberOfEvents = maxNumberOfEvents;
    }

    void start() {

        /** add all of the events which are read from the input trajectory*/

        for (Trajectory t : trajectory) {
            for (int i =0; i<t.inputs.length; i++) {
                //tell each of the input ports/models about what it will be receiving
                Model startingPoint = network.findConnectedModel(i);
                String startingModelName = network.getModelName(startingPoint);
                network.events.insert(new Event(startingPoint, new Time(t.time, 0), "external", startingModelName, t.inputs[i]));
            }
        }

        int eventsExecuted = 0;

        Event nextEvent = network.events.remove();

        while (nextEvent != null && eventsExecuted < this.maxNumberOfEvents) {

            currentTime = nextEvent.time; //advance time
            executeEvent(nextEvent,currentTime); //execute event
            network.passPipeValues(); //pass values around

            ArrayList<Event> generatedEvents = network.generateEvents(nextEvent);
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

    public void executeEvent(Event e, Time currentTime) {

        if (e.action.equals("internal")) {
            String res = e.model.lambda();
            System.out.println(e.time.toString() + " " + e.modelName + ": " + res);
            if (e.model.out == this.network.out) {
                System.out.println("           " + e.time.toString() + " Network: " + res);
            }
            e.model.internalTransition();
        } else if (e.action.equals("external")) {
            e.model.externalTransition(currentTime, e.input);
        } else if (e.action.equals("confluent")) {
            String res = e.model.lambda();
            System.out.println(e.time.toString() + " " + e.modelName + ": " + res);
            if (e.model.out == this.network.out) {
                System.out.println("           " + e.time.toString() + " Network: " + res);
            }
            e.model.confluentTransition(currentTime, e.input);
        }

    }


}