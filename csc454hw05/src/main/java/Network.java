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
    Model startingModel;

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

    }

   ArrayList<Event> generateEvents(Event previousEvent){
       ArrayList<Event> events = new ArrayList<>();

         if (previousEvent.action.equals("internal")) {

            //we will create an external event for whatever is connected to its pipe
            Model other = findConnectedModel(previousEvent.model.out);
            String modelName = getModelName(other);
            if (other != null) {
                Event e = new Event(other, previousEvent.time, "external", modelName, "");
                events.add(e);
            }
            //also create a new event for ourselves (aka, to process more parts)
            Event ourOwnNextEvent = new Event(previousEvent.model, previousEvent.model.timeAdvance(), "internal", previousEvent.modelName, "");
            events.add(ourOwnNextEvent);

        } else if (previousEvent.action.equals("external")) {
            //create an internal transition for ourselves
            Event e = new Event(previousEvent.model,previousEvent.model.timeAdvance(), "internal",  previousEvent.modelName, "" );
            events.add(e);
        }

       return events;

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

    Model findConnectedModel(Port out) {
        Port inPort = null;
        for (Pipe p: pipes) {
            if (p.sending == out) {
                inPort = p.receiving;
                break;
            }
        }

        if (inPort != null) {
          for (Entry<String, Model> model : children.entrySet())  {
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

    public boolean isDone() {
        for (Entry<String, Model> model : children.entrySet()) {
            if (model.getValue().numberOfPartsToProcess != 0) {
                return false;
            }
        }
        return true;
    }
}