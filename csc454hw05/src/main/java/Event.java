public class Event implements Comparable<Event> {

    Model model;
    String action;
    Time time;

    Event(Model m, Time t, String a) {
        model = m;
        time = t;
        action = a;
    }

    @Override
    public int compareTo(Event e) {
        return time.compareTo(e.time);
    }

    @Override
    public String toString() {
        return this.time.toString() + " " + this.model.toString() + " " + action;
    }

    public void executeEvent(Time elapsedTime) {

        if (this.action.equals("internal")) {
            System.out.println(model.lambda());
            model.internalTransition();
        } else if (this.action.equals("external")) {
            model.externalTransition(elapsedTime);
        } else if (this.action.equals("confluent")) {
            System.out.println(model.lambda());
            model.confluentTransition(elapsedTime);
        }

    }

}