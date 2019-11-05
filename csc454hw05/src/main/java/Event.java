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
        return this.time.toString() + " " + this.modelName+ ", action: " + action;
    }

    public void executeEvent(Time elapsedTimeSincePrevEvent) {

        //System.out.println(this.toString());

        if (this.action.equals("internal")) {
            System.out.println(this.time.toString() + " " + model.lambda());
            model.internalTransition();
        } else if (this.action.equals("external")) {
            model.externalTransition(elapsedTimeSincePrevEvent, input);
        } else if (this.action.equals("confluent")) {
            System.out.println(this.time.toString() + " " + model.lambda());
            model.con = true;
            model.confluentTransition(elapsedTimeSincePrevEvent, input);
            model.con = false;
        }

    }

    public boolean isSameEventAs(Event other) {
        return other.modelName.equals(modelName) && other.time.equals(time) && other.action.equals(action);
    }

}