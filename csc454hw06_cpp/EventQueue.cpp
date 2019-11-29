#ifndef EVENT_QUEUE
#define EVENT_QUEUE

#include "Event.cpp"
class EventQueue  {
public: 
    Event *pQueue;
    int queueSize;
     int index;

     EventQueue(int capacity){
        pQueue = new Event[capacity];
         queueSize = capacity;
    }

     bool insert(Event item ){
        if(index == pQueue.length){
            return false;
        }
        pQueue[index] = item;
        index++;
        return true;
    }

     Event peek(){
        Event e = remove();
        insert(e);
        return e;
    }

     int getNumberOfElements(){
        return index;
    }

    Event remove(){
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
};


#endif