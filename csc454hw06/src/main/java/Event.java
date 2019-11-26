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
        if (time.compareTo(e.time) == 0) {
            return this.modelName.compareTo(e.modelName);
        } else {
            return time.compareTo(e.time);
        }
    }

    @Override
    public String toString() {
        return this.time.toString() + " " + this.modelName+ ", action: " + action;
    }

    public void executeEvent(Time currentTime) {

        //System.out.println(this.toString());

        if (this.action.equals("internal")) {
            System.out.println(this.time.toString() + " " + model.lambda());
            model.internalTransition();
        } else if (this.action.equals("external")) {
            model.externalTransition(currentTime, input);
        } else if (this.action.equals("confluent")) {
            System.out.println(this.time.toString() + " " + model.lambda());
            model.confluentTransition(currentTime, input);
        }

    }

    public boolean isSameEventAs(Event other) {
        return other.modelName.equals(modelName) && other.time.equals(time) && other.action.equals(action);
    }

}