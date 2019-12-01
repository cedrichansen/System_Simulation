#ifndef EVENT_QUEUE
#define EVENT_QUEUE

#include "Event.cpp"

template <class IN, class OUT>
class EventQueue  {
public: 
    Event<IN, OUT> ** pQueue;
    int queueSize;
    int index;

     EventQueue(int capacity){
        pQueue = new Event<IN, OUT>*[capacity];
        queueSize = capacity;
    }

     bool insert(Event<IN, OUT> *item ){
        if(index == queueSize){
            return false;
        }
        pQueue[index] = item;
        index++;
        return true;
    }

     Event<IN, OUT> * peek(){
        Event<IN, OUT>* e = remove();
        insert(e);
        return e;
    }

     int getNumberOfElements(){
        return index;
    }

    Event<IN, OUT> *remove(){
        if(index == 0){
            return NULL;
        }
        int minIndex = 0;
        for (int i=1; i<index; i++) {
            if (pQueue[i]->compareTo(pQueue[minIndex]) < 0) {
                minIndex = i;
            }
        }
        Event<IN, OUT> * result = pQueue[minIndex];
        index--;
        pQueue[minIndex] = pQueue[index];
        return result;
    }
};


#endif