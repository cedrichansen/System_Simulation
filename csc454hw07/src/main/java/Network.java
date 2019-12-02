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

    public Network(Port<IN> input, Port<OUT> out) {
        this.in = new Port[1];
        this.in[0] = input;
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

    //Only send the value that was created by the previous models delta
    void passPipeValues(Port out) {
        for (Pipe p : pipes) {
            if (p.sending.currentValue != null && p.sending == out) {
                p.passValue();
            }
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
     *
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


    ArrayList<Model> findModelsToCreateEventFor(Port o) {
        ArrayList<Model> models = new ArrayList<>();
        ArrayList<Port> validPorts = new ArrayList<>();
        for (Pipe p : pipes) {
            if (p.sending == o) {
                validPorts.add(p.receiving);
            }
        }

        if (validPorts.size() != 0) {
            for (Entry<String, Model> model : children.entrySet()) {
                for (Port p : model.getValue().in) {
                    for (Port op : validPorts) {
                        if (p == op) {
                            models.add(model.getValue());
                        }
                    }
                }
            }
        }
        return models;
    }

    ArrayList<Event> generateEvents(Event event) {
        ArrayList<Event> events = new ArrayList<>();

        if (event.action.equals("internal") || event.action.equals("confluent")) {

            //we will create an external event for whatever is connected to its pipe
            ArrayList<Model> otherModels = findModelsToCreateEventFor(event.model.out);
            for (Model other : otherModels) {
                String modelName = getModelName(other);
                if (other != null) {
                    Event e = new Event(other, event.time, "external", modelName, "");
                    events.add(e);
                }
            }

            //create an internal transition for ourselves if needed
            Time modelAdvance = event.model.timeAdvance();
            if (modelAdvance.realTime != event.model.getMaxTimeAdvance()) {
                Time eventTime = new Time(event.time.realTime + modelAdvance.realTime, 0);
                Event ourOwnNextEvent = new Event(event.model, eventTime, "internal", event.modelName, "");
                events.add(ourOwnNextEvent);
            }
        } else if (event.action.equals("external")) {
            //remove the old internal event.. It may no longer be correct
            this.events = removeInternalEvent(event.model);

            //Create a new internal event. It may or may not create the same event that was just deleted
            Time modelAdvance = event.model.timeAdvance();
            if (modelAdvance.realTime != event.model.getMaxTimeAdvance()) {
                Time eventTime = new Time(event.time.realTime + modelAdvance.realTime, 0);
                Event e = new Event(event.model, eventTime, "internal", event.modelName, "");
                events.add(e);
            }
        }

        return events;
    }


    /**
     * creates confluent events, and adds it to list, and also deletes the required internal and external events
     */
    EventQueue createConfluentEvent() {

        EventQueue updatedEvents = new EventQueue(this.events.pQueue.length);

        Time t = events.peek().time;
        Event current = events.remove();
        Event eventAfter = events.peek();

        while (current != null) {

            if (eventAfter == null) {
                //the current event is the last event, so it can't be confluent. add it
                updatedEvents.insert(current);
            } else {
                //we have *At least* 2 elements of interest, so check if confluent case
                // Events are ordered on basis of time, modelName, and action, so we can be assured they are ordered as such
                //we can only create confluent events for events at the next moment in time
                if (current.time.realTime == t.realTime && current.time.discreteTime == t.discreteTime && eventAfter.time.realTime == t.realTime && eventAfter.time.discreteTime == t.discreteTime) {
                    if (current.modelName.equals(eventAfter.modelName)) {
                        if ((current.action.equals("internal") && eventAfter.action.equals("external")) || (current.action.equals("external") && eventAfter.action.equals("internal"))) {
                            //we have found a confluent case
                            events.remove(); //this will remove the eventAfter

                            String input = current.input.equals("") ? eventAfter.input : current.input;
                            Event con = new Event(current.model, current.time, "confluent", current.modelName, input);
                            updatedEvents.insert(con);
                        }
                    } else {
                        updatedEvents.insert(current);
                    }
                } else {
                    updatedEvents.insert(current);
                }

            }

            //make progress on the loop... Look at each object
            current = events.remove();
            eventAfter = events.peek();
        }

        return updatedEvents;

    }

    EventQueue removeInternalEvent(Model m) {
        EventQueue validEvents = new EventQueue(this.events.pQueue.length);
        Event e = this.events.remove();

        while (e != null) {
            if (!(e.model == m && e.action.equals("internal"))) {
                validEvents.insert(e);
            }
            e = this.events.remove();
        }
        return validEvents;
    }


    @Override
    public String toString() {
        return "Network -- events:" + this.events.getNumberOfElements();
    }


}