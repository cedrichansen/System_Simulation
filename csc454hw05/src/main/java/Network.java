import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;

public class Network extends Model {

    Map<String, Model> children;
    EventQueue events = new EventQueue();
    ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    Port <Integer> [] in;
    Port <Integer> out;

    public Network(Port<Integer>[] inputs, Port<Integer> out) {
        this.in = inputs;
        this.out = out;
        children = new HashMap<>();
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

    void addEventsToPriorityQueue(Time t) {
        prevKnownTime = t;
        for (Entry<String, Model> model : children.entrySet()) {

            //remove whatever we had previously for the model, because it likely needs to be recalculated
            model.getValue().parent.events.removeEventsForModel(model.getValue(), model.getKey());

            //add the new event back in
            Time modelAdvance = model.getValue().timeAdvance();
            Event e;
            if (modelAdvance.realTime == model.getValue().getMaxTimeAdvance()) {
                e = new Event(model.getValue(), prevKnownTime.timeAdvance(modelAdvance), "nothing", model.getKey(), "");
            } else {
                e = new Event(model.getValue() ,prevKnownTime.timeAdvance(modelAdvance), "internal", model.getKey(), "");
            }
            events.add(e);
        }
    }


    public String lambda() {
        return null;
    }


    public void externalTransition(Time inputTime, String in) {
        //TODO: Tell the first model that we got something
        //in represents the amount of new parts we got (as integer)
        int partsAdded = Integer.parseInt(in);
        this.in[0].currentValue = partsAdded;

        passPipeValues();

        for (Entry<String, Model> model : children.entrySet()) {
           model.getValue().externalTransition(inputTime, in);
        }
    }

    public void internalTransition() {

    }

    public void confluentTransition(Time inputTime, String in) {

    }

    public Time timeAdvance() {
        return events.peek().time;
    }

    public double getMaxTimeAdvance() {
        return 0;
    }

    public void addParent(Network n) {

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Network -- Current event queue size: " + this.events.events.size();
    }
}