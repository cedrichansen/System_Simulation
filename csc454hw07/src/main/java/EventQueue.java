public class EventQueue  {

    public Event[] pQueue;
    private int index;

    public EventQueue(int capacity){
        pQueue = new Event[capacity];
    }

    public boolean insert(Event item ){
        if(index == pQueue.length){
            return false;
        }
        pQueue[index] = item;
        index++;
        return true;
    }

    public Event peek(){
        Event e = remove();
        insert(e);
        return e;
    }

    public int getNumberOfElements(){
        return index;
    }

    public Event remove(){
        if(index == 0){
            return null;
        }
        int minIndex = 0;
        for (int i=1; i<index; i++) {
            if (pQueue[i].compareTo(pQueue[minIndex]) < 0) {
                minIndex = i;
            }
        }
        Event result = pQueue[minIndex];
        index--;
        pQueue[minIndex] = pQueue[index];
        return result;
    }
}
