import java.util.ArrayList;

public class Framework {

    Network network;
    ArrayList<Trajectory> trajectory;

    Time timeElapsed;
    Time timeSincePreviousEvent;
    Time previousEventTime;


    public Framework(Network m, ArrayList<Trajectory> trajectory) {
        this.network = m;
        this.trajectory = trajectory;
        timeElapsed = new Time(0, 0);
        timeSincePreviousEvent = new Time(0, 0);
        previousEventTime = new Time(0, 0);
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


        while (!network.isDone() || network.events.getNumberOfElements() != 0) {

            Event nextEvent = network.events.remove();
            nextEvent.executeEvent(timeElapsed);
            network.passPipeValues();

            //ArrayList<Event> generatedEvents = network.generateEvents(nextEvent);
//            for (Event e : generatedEvents) {
//                network.events.add(e);
//            }
            previousEventTime = timeElapsed;
            timeElapsed = nextEvent.time;
            timeSincePreviousEvent = new Time(timeElapsed.realTime - previousEventTime.realTime, timeElapsed.discreteTime - previousEventTime.discreteTime > 0 ? timeElapsed.discreteTime - previousEventTime.discreteTime : 0);

            //network.events.events = network.createConfluentEvent();
        }


        System.out.println("\n\nSimulation complete");
    }


}