import java.util.ArrayList; import java.util.Collections;

public class EventQueue {

    ArrayList<Event> events = new ArrayList<Event>();

    public void add(Event e) {
        events.add(e);
        Collections.sort(events);
    }

    public Event remove(){
        if (events.size() != 0) {
            return events.remove(0);
        }
        return null;
    }

    public Event peek(){
        if (events.size() != 0) {
            return events.get(0);
        }
        return null;
    }

    
    public void removeEventsForModel(Model m, String modelName){
        for (Event e : events) {
            if (e.model == m) {
                events.remove(e);
            }
            if (e.modelName.equals(modelName)) {
                events.remove(e);
            }
        }
    }
}