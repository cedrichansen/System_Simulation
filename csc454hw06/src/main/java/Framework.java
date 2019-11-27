import java.util.ArrayList;

public class Framework {

    Network network;
    ArrayList<Trajectory> trajectory;

    Time currentTime;


    public Framework(Network m, ArrayList<Trajectory> trajectory) {
        this.network = m;
        this.trajectory = trajectory;
       currentTime = new Time(0,0);
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

        Event nextEvent = network.events.remove();

        while (nextEvent != null) {

            currentTime = nextEvent.time; //advance time
            nextEvent.executeEvent(currentTime); //execute event
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

        }


        System.out.println("\n\nSimulation complete");
    }


}