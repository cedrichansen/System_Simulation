
#if !defined(NETWORK)
#define NETWORK

#include "EventQueue.cpp"
#include "Port.cpp"
#include "Time.cpp"
#include <vector>
#include <map>
#include "Model.cpp"
#include "Pipe.cpp"

using namespace std;

template <class IN, class OUT>
class Network
{
public:
    EventQueue *events;
    map<string, void *> *children;
    map<string, void *> *pipes;
    Port<IN> *in;
    Port<OUT> *out;
    Time prevKnownTime;
    int numInputs;

    Network(Port<IN> *inputs, Port<OUT> *out, int numInputs)
    {
        this.numInputs = numInputs;
        this.in = inputs;
        this.out = out;
        pipes = new map<string, Pipe *>();
        children = new map<string, Model *>();
        prevKnownTime = new Time(0, 0);
        events = new EventQueue(100);
    }

    void addModel(string modelName, void *m)
    {
        children->insert(pair<string, Model<> *>(modelName, m));
    }

    void addPipe(string pipeName, Pipe<> *pipe)
    {
        pipes->insert(pair<string, Pipe<>*>(pipeName, pipe));
    }

    void passPipeValues()
    {
        map<string, Pipe *>::iterator itr;
        for (itr = pipes->begin(); itr != pipes->end(); itr++)
        {
            if (itr->second->currentValue != NULL)
            {
                itr->second->passValue();
            }
        }
    }

    std::string getModelName(Model *m)
    {

        map<string, Pipe *>::iterator itr;
        for (itr = children->begin(); itr != children->end(); itr++)
        {
            if (itr->second == m)
            {
                return itr->first;
            }
        }
        return "";
    }

    /**
     * We pass in what index of the input port we want, and we return the model that owns that port
     * @return the model connected to the port
     */
    Model * findConnectedModel(int i)
    {
        
        Port initial = in[i];

        map<string, Pipe *>::iterator itr;
        for (itr = children->begin(); itr != children->end(); itr++)
        {
            for (int i = 0; i< itr->second->numInputs ; i++) {
                if (itr->second->numInputs[i] == initial) {
                    return itr->second;
                }
            }
        }

        printf("Cannot find connected model");
        return NULL;

    }

    Model findModelToCreateEventFor(Port<> o)
    {
        Port inPort = null;
        for (Pipe p : pipes)
        {
            Port s = p.sending;
            if (s == o)
            {
                inPort = p.receiving;
                break;
            }
        }

        if (inPort != null)
        {
            for (Entry<String, Model> model : children.entrySet())
            {
                for (Port p : model.getValue().in)
                {
                    if (p == inPort)
                    {
                        return model.getValue();
                    }
                }
            }
        }
        return null;
    }

    vector<Event> generateEvents(Event event)
    {
        vector<Event> events = new vector<Event>();

        if (event->action.compare("internal") == 0 || event->action.compare("confluent") == 0)
        {

            //we will create an external event for whatever is connected to its pipe
            Model other = findModelToCreateEventFor(event->model->out);
            string modelName = getModelName(other);
            if (other != null)
            {
                Event e = new Event(other, event->time, "external", modelName, "");
                events.push_back(e);
            }
            //create an internal transition for ourselves if needed
            Time modelAdvance = event->model->timeAdvance();
            if (modelAdvance->realTime != event->model->getMaxTimeAdvance())
            {
                Time eventTime = new Time(event.time.realTime + modelAdvance.realTime, 0);
                Event ourOwnNextEvent = new Event(event.model, eventTime, "internal", event.modelName, "");
                events.add(ourOwnNextEvent);
            }
        }
        else if (event->action.compare("external") == 0)
        {
            //remove the old internal event.. It may no longer be correct
            events = removeInternalEvent(event->model);

            //Create a new internal event. It may or may not create the same event that was just deleted
            Time modelAdvance = event->model->timeAdvance();
            Time eventTime = new Time(event->time.realTime + modelAdvance->realTime, 0);
            Event e = new Event(event.model, eventTime, "internal", event.modelName, "");
            events.push_back(e);
        }

        return events;
    }

    /**
     * creates confluent events, and adds it to list, and also deletes the required internal and external events
     */
    EventQueue * createConfluentEvent()
    {

        EventQueue * updatedEvents = new EventQueue(events->getNumberOfElements);

        Time t = events.peek().time;
        Event * current = events.remove();
        Event * eventAfter = events.peek();

        while (current != null)
        {

            if (eventAfter == null)
            {
                //the current event is the last event, so it can't be confluent. add it
                updatedEvents->insert(current);
            }
            else
            {
                if (current->time->realTime == t->realTime && current->time->discreteTime == t->discreteTime && eventAfter->time->realTime == t->realTime && eventAfter->time->discreteTime == t->discreteTime)
                {
                    if (current->modelName.compare(eventAfter->modelName) == 0)
                    {
                        if ((current.action.compare("internal") == 0 && eventAfter->action.compare("external") == 0) || (current->action.compare("external") == 0 && eventAfter->action.compare("internal") == 0))
                        {
                            events.remove(); //this will remove the eventAfter

                            string input = current->input.compare("") == 0 ? eventAfter->input : current->input;
                            Event con = new Event(current.model, current.time, "confluent", current.modelName, input);
                            updatedEvents.insert(con);
                        }
                    }
                    else
                    {
                        updatedEvents.insert(current);
                    }
                }
                else
                {
                    updatedEvents.insert(current);
                }
            }

            //make progress on the loop... Look at each object
            current = events.remove();
            eventAfter = events.peek();
        }

        return updatedEvents;
    }

    EventQueue removeInternalEvent(Model m)
    {
        EventQueue validEvents = new EventQueue(this.events.pQueue.length);
        Event e = this.events.remove();

        while (e != null)
        {
            if (!(e.model == m && e.action.equals("internal")))
            {
                validEvents.insert(e);
            }
            e = this.events.remove();
        }
        return validEvents;
    }

    @Override public String toString()
    {
        return "Network -- events:" + this.events.getNumberOfElements();
    }
};

#endif // NETWORK
