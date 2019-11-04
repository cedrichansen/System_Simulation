import java.util.ArrayList;

public class Framework {

    Network network;
    ArrayList<String[]> trajectory;


    Time timeElapsed;
    Time timeAdvance;


    public Framework(Network m, ArrayList<String[]> trajectory) {
        this.network = m;
        this.trajectory = trajectory;

        timeElapsed = new Time(0, 0);
    }


    void start() throws InterruptedException {

        /** add all of the events which are read from the input trajectory*/
        for (int i = 0; i<trajectory.size(); i++) {
            network.events.add(new Event(network, new Time(Double.parseDouble(trajectory.get(i)[0]) + timeElapsed.realTime, 0), "external", "network",trajectory.get(i)[1] ));
        }


        while(!network.events.peek().action.equals("nothing")) {

            network.addEventsToPriorityQueue(timeElapsed);

            Event nextEvent = network.events.remove();
            timeAdvance = nextEvent.time;

            System.out.println("Advancing to: " + (timeAdvance.realTime + timeElapsed.realTime));

            timeElapsed = timeElapsed.timeAdvance(timeAdvance);

            nextEvent.executeEvent(timeElapsed);

        }

        System.out.println("\n\nSimulation complete");
    }


}