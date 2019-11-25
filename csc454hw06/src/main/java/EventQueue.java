public class EventQueue  {

    public Event[] pQueue;
    private int index;

    public EventQueue(int capacity){
        pQueue = new Event[capacity];
    }

    public void insert(Event item ){
        if(index == pQueue.length){
            return;
        }
        pQueue[index] = item;
        index++;
    }

    public int getNumberOfElements(){
        return index;
    }

    public Event remove(){
        if(index == 0){
            return null;
        }
        int maxIndex = 0;
        for (int i=1; i<index; i++) {
            if (pQueue[i].compareTo(pQueue[maxIndex]) > 0) {
                maxIndex = i;
            }
        }
        Event result = pQueue[maxIndex];
        index--;
        pQueue[maxIndex] = pQueue[index];
        return result;
    }
}
