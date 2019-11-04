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
        prevKnownTime = new Time(0,0);
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

    void addEventsToPriorityQueue(Time totalElapsed) {
        Time timeSinceLastInput = new Time(totalElapsed.realTime - prevKnownTime.realTime, 0);
        prevKnownTime = totalElapsed;
        for (Entry<String, Model> model : children.entrySet()) {

            //remove whatever we had previously for the model, because it likely needs to be recalculated
            this.events = model.getValue().parent.events.updateEventsForModel(model.getValue(), model.getKey());

            if (model.getValue().canPerformExternalTransition()) {
                //someone gave me something! need to add an external transition
                Event e = new Event(model.getValue(), prevKnownTime.timeAdvance(new Time(prevKnownTime.realTime,1)), "external", model.getKey(), "");
                events.add(e);
            }


            //add the new internal transition if we need to
            Time modelAdvance = model.getValue().timeAdvance();
            Event e;
            if (modelAdvance.realTime != model.getValue().getMaxTimeAdvance()) {
                //TODO: update the internal clock of the model with the time that we know about...
                Time eventTime = (new Time(prevKnownTime.realTime, 0)).timeAdvance(modelAdvance);
                e = new Event(model.getValue(), eventTime, "internal", model.getKey(), "");
                events.add(e);
                model.getValue().modifyInternalClock(timeSinceLastInput);
            }
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
        return "Network -- events:" + this.events.events.size();
    }

    @Override
    public boolean canPerformExternalTransition() {
        for (int i=0; i<in.length; i++) {

            if (in[i].currentValue != null) {
                if (in[i].currentValue != 0) {
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public void modifyInternalClock(Time sinceLastInput) {

    }
}