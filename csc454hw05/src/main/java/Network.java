import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Network extends Model {

    Map<String, Model> children;
    PriorityQueue<Event> events = new PriorityQueue<Event>();

    public Network(Port [] inputs, Port out) {
        this.in = inputs;
        this.out = out;
        children = new HashMap<>();
    }

    void add(Model m, String name) {
        children.put(name, m);
        m.addParent(this);
    }

    public String lambda() {
        return null;
    }

    public void externalTransition(Time inputTime) {

    }

    public void internalTransition() {

    }

    public void confluentTransition(Time inputTime) {

    }

    public Time timeAdvance() {
        return null;
    }

    public double getMaxTimeAdvance() {
        return 0;
    }

    public void addParent(Network n) {

    }
}