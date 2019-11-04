public class Event implements Comparable<Event> {

    Model model;
    String action;
    Time time;
    String modelName;
    String input;

    Event(Model m, Time t, String a, String modelName, String input) {
        model = m;
        time = t;
        action = a;
        this.modelName = modelName;
        this.input = input;
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
            model.externalTransition(elapsedTime, input);
        } else if (this.action.equals("confluent")) {
            System.out.println(model.lambda());
            model.confluentTransition(elapsedTime, input);
        }

    }

}