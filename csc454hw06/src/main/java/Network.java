import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;

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

    Model findModelToCreateEventFor(Port o) {
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

    ArrayList<Event> generateEvents(Event event) {
        ArrayList<Event> events = new ArrayList<>();

        if (event.action.equals("internal") || event.action.equals("confluent")) {

            //we will create an external event for whatever is connected to its pipe
            Model other = findModelToCreateEventFor(event.model.out);
            String modelName = getModelName(other);
            if (other != null) {
                Event e = new Event(other, event.time, "external", modelName, "");
                events.add(e);
            }
            //create an internal transition for ourselves if needed
            Time modelAdvance = event.model.timeAdvance();
            if (modelAdvance.realTime != event.model.getMaxTimeAdvance()) {
                Time eventTime = new Time(event.time.realTime + modelAdvance.realTime, 0);
                Event ourOwnNextEvent = new Event(event.model, eventTime, "internal", event.modelName, "");
                events.add(ourOwnNextEvent);
            }
        }
        if (event.action.equals("external") || event.action.equals("confluent")) {
            //remove the old internal event.. It may no longer be correct
            this.events = removeInternalEvent(event.model);

            //Create a new internal event. It may or may not create the same event that was just deleted
            Time modelAdvance = event.model.timeAdvance();
            Time eventTime = new Time(event.time.realTime + modelAdvance.realTime, 0);
            Event e = new Event(event.model, eventTime, "internal", event.modelName, "");
            events.add(e);
        }

        return events;
    }


    /**
     * creates confluent events, and adds it to list, and also deletes the required internal and external events
     */
    ArrayList<Event> createConfluentEvent() {

        Event next = this.events.remove();
        this.events.insert(next);
        ArrayList<Event> eventsAtTheSameTime = getEventsAtTime(next.time);

        for (Entry<String, Model> model : children.entrySet()) {
            boolean hasExternalTrans = false;
            boolean hasInternalTrans = true;

            for (Event e : eventsAtTheSameTime) {
                if (e.model == model.getValue()) {
                    if (e.action.equals("internal")) {
                        hasInternalTrans = true;
                    } else if (e.action.equals("external")) {
                        hasExternalTrans = true;
                    }
                }
            }

            if (hasExternalTrans && hasInternalTrans) {
                //we need to remove them and add a confluent event instead
            }
        }





    }

    ArrayList<Event> getEventsAtTime(Time t) {
        ArrayList<Event> eventsAtTime = new ArrayList<>();
        for (int i =0 ; i < this.events.getNumberOfElements(); i++) {
            if (this.events.pQueue[i].time.realTime == t.realTime && this.events.pQueue[i].time.discreteTime == t.discreteTime) {
                //event is at the same time
                eventsAtTime.add(this.events.pQueue[i]);
            }
        }
        return eventsAtTime;
    }



    EventQueue removeInternalEvent(Model m) {
        EventQueue validEvents = new EventQueue(this.events.pQueue.length);
        for (int i =0; i<this.events.getNumberOfElements(); i++) {
            Event e = this.events.pQueue[i];
            if (!(e.model == m && e.action.equals("internal"))) {
                validEvents.insert(e);
            }
        }
        return validEvents;
    }


    @Override
    public String toString() {
        return "Network -- events:" + this.events.getNumberOfElements();
    }



}