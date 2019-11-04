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

    
    public EventQueue updateEventsForModel(Model m, String modelName){
        EventQueue updatedEvents = new EventQueue();
        for (Event e : events) {
            if (!e.modelName.equals(modelName)) {
                updatedEvents.add(e);
            }
        }
        return updatedEvents;
    }
}