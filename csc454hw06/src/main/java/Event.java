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
            if (this.modelName.compareTo(e.modelName) == 0) {
                return this.action.compareTo(e.action);
            } else {
                return this.modelName.compareTo(e.modelName);
            }
        } else {
            return time.compareTo(e.time);
        }
    }

    @Override
    public String toString() {
        return this.time.toString() + " " + this.modelName + ", action: " + action;
    }


}