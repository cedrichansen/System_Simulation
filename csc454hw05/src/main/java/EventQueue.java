import java.util.ArrayList; import java.util.Collections;

public class EventQueue {

    ArrayList<Event> events = new ArrayList<Event>();

    public void add(Event e) {
        if (!contains(e)) {
            events.add(e);
            Collections.sort(events);
        }
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

    public boolean contains(Event e) {
        for (int i = 0; i<this.events.size(); i++) {
            Event ev = events.get(i);
            if (e.isSameEventAs(ev)) {
                return true;
            }
        }
        return false;
    }

}