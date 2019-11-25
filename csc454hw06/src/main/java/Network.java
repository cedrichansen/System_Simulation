import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Network<IN, OUT> {

    Map<String, Model> children;
    EventQueue events;
    ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    Port<IN>[] in;
    Port<OUT> out;
    Time prevKnownTime;

    public Network(Port<IN>[] inputs, Port<OUT> out) {
        this.in = inputs;
        this.out = out;
        children = new HashMap<>();
        prevKnownTime = new Time(0, 0);
        events = new EventQueue(100);
    }

    void addChild(Model m, String name) {
        children.put(name, m);
        m.addParent(this);
    }

    void addPipe(Pipe p) {
        pipes.add(p);
    }

    void passPipeValues() {
        for (Pipe p : pipes) {
            p.passValue();
        }
    }



    String getModelName(Model m) {
        for (Entry<String, Model> model : children.entrySet()) {
            if (model.getValue() == m) {
                return model.getKey();
            }
        }
        return "";
    }


    /**
     * We pass in what index of the input port we want, and we return the model that owns that port
     * @return the model connected to the port
     */
    Model findConnectedModel(int i) {
        Port initial = in[i];

            for (Entry<String, Model> model : children.entrySet()) {
                for (Port p : model.getValue().in) {
                    if (p == initial) {
                        return model.getValue();
                    }
                }
            }
        System.out.println("Cannot find connected model!");
            return null;
    }




    @Override
    public String toString() {
        return "Network -- events:" + this.events.getNumberOfElements();
    }


    public boolean isDone() {
        for (Entry<String, Model> model : children.entrySet()) {
            if (model.getValue().numberOfPartsToProcess != 0) {
                return false;
            }
        }
        return true;
    }
}