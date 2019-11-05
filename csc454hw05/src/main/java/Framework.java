import java.util.ArrayList;

public class Framework {

    Network network;
    ArrayList<String[]> trajectory;

    Time timeElapsed;
    Time timeSincePreviousEvent;
    Time previousEventTime;


    public Framework(Network m, ArrayList<String[]> trajectory) {
        this.network = m;
        this.trajectory = trajectory;
        timeElapsed = new Time(0, 0);
        timeSincePreviousEvent = new Time(0,0);
        previousEventTime = new Time(0,0);
    }


    void start() {

        /** add all of the events which are read from the input trajectory*/
        for (int i = 0; i<trajectory.size(); i++) {
            Model startingPoint = network.findStartingPoint();
            String startingModelName = network.getModelName(startingPoint);
            network.events.add(new Event(startingPoint, new Time(Double.parseDouble(trajectory.get(i)[0]) + timeElapsed.realTime, 0), "external", startingModelName,trajectory.get(i)[1] ));
        }


        while(!network.isDone() || network.events.peek() != null) {

            Event nextEvent = network.events.remove();
            nextEvent.executeEvent(timeSincePreviousEvent);
            network.passPipeValues();

            ArrayList<Event> generatedEvents = network.generateEvents(nextEvent);
            for (Event e : generatedEvents) {
                network.events.add(e);
            }
            previousEventTime = timeElapsed;
            timeElapsed = nextEvent.time;
            timeSincePreviousEvent = new Time(timeElapsed.realTime - previousEventTime.realTime, timeElapsed.discreteTime-previousEventTime.discreteTime > 0 ? timeElapsed.discreteTime-previousEventTime.discreteTime : 0);

            network.events.events = network.createConfluentEvent();
        }


        System.out.println("\n\nSimulation complete");
    }


}