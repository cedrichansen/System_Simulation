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


    void start() throws InterruptedException {

        /** add all of the events which are read from the input trajectory*/
        for (int i = 0; i<trajectory.size(); i++) {
            network.events.add(new Event(network, new Time(Double.parseDouble(trajectory.get(i)[0]) + timeElapsed.realTime, 0), "external", "network",trajectory.get(i)[1] ));
        }


        while(!network.isDone() || network.events.peek() != null) {

            if (network.events.peek() != null) {
                while (network.events.peek().time.equals(timeElapsed)) {
                    //multiple events happened at the exact same time! Need to perform both
                    Event e = network.events.remove();
                    e.executeEvent(timeSincePreviousEvent);
                    network.passPipeValues();
                    System.out.println();
                }
            }

            network.addEventsToPriorityQueue(timeElapsed);

            Event nextEvent = network.events.remove();

            previousEventTime = timeElapsed;
            timeElapsed = nextEvent.time;
            timeSincePreviousEvent = new Time(timeElapsed.realTime - previousEventTime.realTime, timeElapsed.discreteTime-previousEventTime.discreteTime > 0 ? timeElapsed.discreteTime-previousEventTime.discreteTime : 0);

            nextEvent.executeEvent(timeSincePreviousEvent);
            network.passPipeValues();
            System.out.println();

        }


        System.out.println("\n\nSimulation complete");
    }


}