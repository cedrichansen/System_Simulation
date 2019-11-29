#ifndef EVENT
#define EVENT

#include <string>
#include "Model.cpp"
#include "Time.cpp"

class Event {

public: 
    void * model;
    std::string action;
    Time * time;
    std::string modelName;
    std::string input;

    Event(void * m, Time * t, std::string a, std::string modelName, std::string input) {
        model = m;
        time = t;
        action = a;
        modelName = modelName;
        input = input;
    }


    int compareTo(Event e) {
        if (time->compareTo(e.time) == 0) {
            if (modelName.compare(e.modelName) == 0) {
                return action.compare(e.action);
            } else {
                return modelName.compare(e.modelName) * -1;
            }
        } else {
            return time->compareTo(e.time);
        }
    }

    std::string toString() {
        return time->toString() + " " + modelName + ", action: " + action;
    }


};


#endif