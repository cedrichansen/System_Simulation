import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;

public class Network extends Model {

    Map<String, Model> children;
    EventQueue events = new EventQueue();
    ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    Port<Integer>[] in;
    Port<Integer> out;
    Model startingModel;

    public Network(Port<Integer>[] inputs, Port<Integer> out) {
        this.in = inputs;
        this.out = out;
        children = new HashMap<>();
        prevKnownTime = new Time(0, 0);
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

    /**
     * creates confluent events, and adds it to list, and also deletes the required internal and external events
     */
    ArrayList<Event> createConfluentEvent() {
        ArrayList<Event> eventsAtTheSameTime = new ArrayList<>();
        Time prev = new Time(0, 0);

        for (Event e : events.events) {
            if (e.time.realTime == prev.realTime && e.time.discreteTime == prev.discreteTime) {
                //check to see if the list of events with us have same model. try to make a confluent event
                for (Event possibleConfluentEv : eventsAtTheSameTime) {
                    if (possibleConfluentEv.modelName.equals(e.modelName)) {
                        if (possibleConfluentEv.action.equals("external") && e.action.equals("internal") || possibleConfluentEv.action.equals("internal") && e.action.equals("external")) {
                            String in;
                            if (!possibleConfluentEv.input.equals("")) {
                                in = possibleConfluentEv.input;
                            } else {
                                in = e.input;
                            }
                            Event confluentEvent = new Event(e.model, e.time, "confluent", e.modelName, in);
                            this.events.add(confluentEvent);
                            return removeInternalAndExternals(confluentEvent);
                        }
                    }
                }

            } else {
                eventsAtTheSameTime = getEventsAtTime(e.time);
                prev = e.time;
            }
        }
        return this.events.events;
    }

    /**
     * We added a confluent event, so remove the internal and external ones
     */
    public ArrayList<Event> removeInternalAndExternals(Event confluentEv) {
        ArrayList<Event> updatedEvents = new ArrayList<>();
        for (Event e : events.events) {
            if (confluentEv.modelName.equals(e.modelName) && (confluentEv.time.realTime == e.time.realTime && confluentEv.time.discreteTime == e.time.discreteTime)) {
                if ((e.action.equals("internal") || e.action.equals("external"))) {
                    //we dont add these
                } else {
                    updatedEvents.add(e);
                }
            } else {
                updatedEvents.add(e);
            }
        }
        return updatedEvents;
    }

    public ArrayList<Event> getEventsAtTime(Time t) {
        ArrayList<Event> ev = new ArrayList<>();
        for (Event e : this.events.events) {
            if (e.time.discreteTime == t.discreteTime && e.time.realTime == t.realTime) {
                ev.add(e);
            }
        }
        return ev;
    }


    ArrayList<Event> generateEvents(Event previousEvent) {
        ArrayList<Event> events = new ArrayList<>();

        if (previousEvent.action.equals("internal") || previousEvent.action.equals("confluent")) {

            //we will create an external event for whatever is connected to its pipe
            Model other = findConnectedModel(previousEvent.model.out);
            String modelName = getModelName(other);
            if (other != null) {
                Event e = new Event(other, previousEvent.time, "external", modelName, "");
                events.add(e);
            }
            //also create a new event for ourselves (aka, to process more parts), and only add it if its not the max
            Time modelAdvance = previousEvent.model.timeAdvance();
            if (modelAdvance.realTime != previousEvent.model.getMaxTimeAdvance()) {
                Time eventTime = new Time(previousEvent.time.realTime + modelAdvance.realTime, 0);
                Event ourOwnNextEvent = new Event(previousEvent.model, eventTime, "internal", previousEvent.modelName, "");
                events.add(ourOwnNextEvent);
            }
        }
        if (previousEvent.action.equals("external") || previousEvent.action.equals("confluent")) {
            //remove the old internal event.. It may no longer be correct
            if (hasInternalTransitionForModel(previousEvent.model)) {
                this.events.events = removeInternalEvent(previousEvent.model);
            }
            //Create a new internal event. It may or may not create the same event that was just deleted
            Time modelAdvance = previousEvent.model.timeAdvance();
            Time eventTime = new Time(previousEvent.time.realTime + modelAdvance.realTime, 0);
            Event e = new Event(previousEvent.model, eventTime, "internal", previousEvent.modelName, "");
            events.add(e);
        }

        return events;
    }

    boolean hasInternalTransitionForModel(Model m) {
        for (Event e : events.events) {
            if (e.model == m && e.action.equals("internal")) {
                return true;
            }
        }
        return false;
    }

    ArrayList<Event> removeInternalEvent(Model m) {
        ArrayList<Event> updatedEvs = new ArrayList<>();
        for (Event e : events.events) {
            if (!(e.model == m && e.action.equals("internal"))) {
                updatedEvs.add(e);
            }
        }
        return updatedEvs;
    }

    String getModelName(Model m) {
        for (Entry<String, Model> model : children.entrySet()) {
            if (model.getValue() == m) {
                return model.getKey();
            }
        }
        return "";
    }

    Model findStartingPoint() {
//        Port in = this.in[0];
//        for (Entry<String, Model> model : children.entrySet()) {
//            if (model.getValue().in[0] == in) {
//                return model.getValue();
//            }
//        }
//        return null;
        return this.startingModel;
    }

    Model findConnectedModel(Port o) {
        Port inPort = null;
        for (Pipe p : pipes) {
            Port s = p.sending;
            if (s == o) {
                inPort = p.receiving;
                break;
            }
        }

        if (inPort != null) {
            for (Entry<String, Model> model : children.entrySet()) {
                for (Port p : model.getValue().in) {
                    if (p == inPort) {
                        return model.getValue();
                    }
                }
            }
        }
        return null;
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
        return "Network -- events:" + this.events.events.size();
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