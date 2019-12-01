
#ifndef NETWORK
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
    map<string, Model<IN, OUT> *> *children;
    map<string, Pipe<IN> *> *pipes;
    Port<IN> *in;
    Port<OUT> *out;
    int numInputs;

    Network(Port<IN> *inputs, Port<OUT> *outp, int numInputs)
    {
        numInputs = numInputs;
        in = inputs;
        out = outp;
        pipes = new map<string, Pipe<IN>*>();
        children = new map<string, Model<IN, OUT> *>();
        events = new EventQueue(100);
    }

    void addModel(Model<IN, OUT> *m, string modelName)
    {
        children->insert(pair<string, Model<IN, OUT> *>(modelName, m));
    }

    void addPipe(Pipe<IN> *pipe, string pipeName)
    {
        pipes->insert(pair<string, Pipe<IN> *>(pipeName, pipe));
    }

    void passPipeValues()
    {
        map<string, Pipe<IN> *>::iterator itr;
        for (itr = pipes->begin(); itr != pipes->end(); itr++)
        {
            if (itr->second->currentValue != NULL)
            {
                itr->second->passValue();
            }
        }
    }

    std::string getModelName(void *m)
    {

        map<string, Pipe<IN> *>::iterator itr;
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
    Model<IN, OUT> * findConnectedModel(int i)
    {

        Port initial = in[i];

        map<string, Model<IN,OUT> *>::iterator itr;
        for (itr = children->begin(); itr != children->end(); itr++)
        {
            for (int i = 0; i < m->numInputs; i++)
            {
                if (m->numInputs[i] == initial)
                {
                    return itr->second;
                }
            }
        }

        printf("Cannot find connected model");
        return NULL;
    }

    Model<IN, OUT> * findModelToCreateEventFor(Port<IN> * o)
    {

        Port inPort = NULL;

        map<string, Pipe<IN> *>::iterator itr;
        for (itr = pipes->begin(); itr != pipes->end(); itr++)
        {
            Port<IN> s = itr->second->sending;
            if (s == o)
            {
                inPort = itr->second->receiving;
                break;
            }
        }

        if (inPort != NULL)
        {
            map<string, Model<IN, OUT> *>::iterator itr2;
            for (itr2 = children->begin(); itr2 != children->end(); itr2++)
            {
                for (int i = 0; i < itr2->second->numInputs; i++)
                {
                    if (itr2->second->in[i] == inPort)
                    {
                        return itr2->second;
                    }
                }
            }
        }
        return NULL;
    }

    vector<Event> generateEvents(Event event)
    {
        vector<Event> events = new vector<Event>();

        if (event.action.compare("internal") == 0 || event.action.compare("confluent") == 0)
        {

            //we will create an external event for whatever is connected to its pipe
            Model<IN, OUT> *  other = findModelToCreateEventFor(event.model->out);
            string modelName = getModelName(other);
            if (other != NULL)
            {
                Event e = new Event(other, event.time, "external", modelName, "");
                events.push_back(e);
            }
            //create an internal transition for ourselves if needed
            Time *  modelAdvance = event.model->timeAdvance();
            if (modelAdvance->realTime != event.model->getMaxTimeAdvance())
            {
                Time eventTime(event.time.realTime + modelAdvance.realTime, 0);
                Event ourOwnNextEvent = new Event(event.model, eventTime, "internal", event.modelName, "");
                events.push_back(ourOwnNextEvent);
            }
            delete modelAdvance;
        }
        else if (event.action.compare("external") == 0)
        {
            //remove the old internal event.. It may no longer be correct
            events = removeInternalEvent(event.model);

            //Create a new internal event. It may or may not create the same event that was just deleted
            Time modelAdvance = event.model->timeAdvance();
            Time eventTime = new Time(event.time.realTime + modelAdvance->realTime, 0);
            Event e = new Event(event.model, eventTime, "internal", event.modelName, "");
            events.push_back(e);
        }

        return events;
    }

    /**
     * creates confluent events, and adds it to list, and also deletes the required internal and external events
     */
    EventQueue *createConfluentEvent()
    {

        EventQueue *updatedEvents = new EventQueue(events->getNumberOfElements);

        Time t = events->peek().time;
        Event *current = events.remove();
        Event *eventAfter = events.peek();

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
                            events->remove(); //this will remove the eventAfter

                            string input = current->input.compare("") == 0 ? eventAfter->input : current->input;
                            Event con = new Event(current.model, current.time, "confluent", current.modelName, input);
                            updatedEvents->insert(con);
                        }
                    }
                    else
                    {
                        updatedEvents->insert(current);
                    }
                }
                else
                {
                    updatedEvents->insert(current);
                }
            }

            //make progress on the loop... Look at each object
            current = events->remove();
            eventAfter = events->peek();
        }

        return updatedEvents;
    }

    EventQueue * removeInternalEvent(Model<IN, OUT> * m)
    {
        EventQueue * validEvents = new EventQueue(events->getNumberOfElements);
        Event * e = events->remove();

        while (e != NULL)
        {
            if (!(e->model == m && e->action.compare("internal") == 0))
            {
                validEvents->insert(e);
            }
            e = events->remove();
        }
        return validEvents;
    }

    string toString()
    {
        return "Network -- events:" + to_string(events.getNumberOfElements());
    }
};

#endif // NETWORK
