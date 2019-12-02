
#ifndef NETWORK
#define NETWORK

#include "EventQueue.cpp"
#include "Port.cpp"
#include "Time.cpp"
#include <vector>
#include <map>
#include "Model.cpp"
#include "Pipe.cpp"
#include <iterator>

using namespace std;

template <class IN, class OUT>
class Network
{

public:
    using modelPtr = Model<IN, OUT> *;
    using pipePtr = Pipe<IN> *;

    EventQueue<IN, OUT> *events;
    map<string, modelPtr> *children;
    map<string, pipePtr> *pipes;
    Port<IN> **in;
    Port<OUT> *out;
    int numInputs;
    typename map<string, pipePtr>::iterator pipeItr;
    typename map<string, modelPtr>::iterator modelItr;

    Network(Port<IN> ** inputs, Port<OUT> *outp, int numIn)
    {
        numInputs = numIn;
        in = inputs;
        out = outp;
        pipes = new map<string, pipePtr>();
        children = new map<string, modelPtr>();
        events = new EventQueue<IN, OUT>(100);
    }

    void addModel(Model<IN, OUT> *m, string modelName)
    {
        children->insert(pair<string, modelPtr>(modelName, m));
    }

    void addPipe(Pipe<IN> *pipe, string pipeName)
    {
        pipes->insert(pair<string, pipePtr>(pipeName, pipe));
    }

    void passPipeValues()
    {
        for (pipeItr = pipes->begin(); pipeItr != pipes->end(); pipeItr++)
        {
            if (pipeItr->second->sending->currentValue != NULL)
            {
                pipeItr->second->passValue();
            }
        }
    }

    std::string getModelName(Model<IN, OUT> *m)
    {

        for (modelItr = children->begin(); modelItr != children->end(); modelItr++)
        {
            if (modelItr->second == m)
            {
                return modelItr->first;
            }
        }
        return "";
    }

    /**
     * We pass in what index of the input port we want, and we return the model that owns that port
     * @return the model connected to the port
     */
    Model<IN, OUT> *findConnectedModel(int i)
    {

        Port<IN> *initial = in[i];

        for (modelItr = children->begin(); modelItr != children->end(); modelItr++)
        {
            for (int i = 0; i < modelItr->second->numberOfInputs; i++)
            {
                if (modelItr->second->in[i] == initial)
                {
                    return modelItr->second;
                }
            }
        }

        printf("Cannot find connected model\n");
        return NULL;
    }

    Model<IN, OUT> *findModelToCreateEventFor(Port<IN> *o)
    {

        Port<IN> * inPort = NULL;

        for (pipeItr = pipes->begin(); pipeItr != pipes->end(); pipeItr++)
        {
            if (pipeItr->second->sending == o)
            {
                inPort = pipeItr->second->receiving;
                break;
            }
        }

        if (inPort != NULL)
        {
            for (modelItr = children->begin(); modelItr != children->end(); modelItr++)
            {
                for (int i = 0; i < modelItr->second->numberOfInputs; i++)
                {
                    if (modelItr->second->in[i] == inPort)
                    {
                        return modelItr->second;
                    }
                }
            }
        }
        return NULL;
    }

    vector<Event<IN, OUT>*> generateEvents(Event<IN, OUT> * event)
    {
        vector<Event<IN, OUT> *> generatedEvents;

        if (event->action.compare("internal") == 0 || event->action.compare("confluent") == 0)
        {
            //we will create an external event for whatever is connected to its pipe
            Model<IN, OUT> *other = findModelToCreateEventFor(event->model->out);
            string modelName = getModelName(other);
            if (other != NULL)
            {
                Event<IN, OUT> *e = new Event<IN, OUT>(other, event->time, "external", modelName, "");
                generatedEvents.push_back(e);
            }
            //create an internal transition for ourselves if needed
            Time *modelAdvance = event->model->timeAdvance();
            if (modelAdvance->realTime != event->model->getMaxTimeAdvance())
            {
                Time *eventTime = new Time(event->time->realTime + modelAdvance->realTime, 0);
                Event<IN, OUT> *ourOwnNextEvent = new Event<IN, OUT>(event->model, eventTime, "internal", event->modelName, "");
                generatedEvents.push_back(ourOwnNextEvent);
            }
            delete modelAdvance;
        }
        else if (event->action.compare("external") == 0)
        {
            //remove the old internal event.. It may no longer be correct
            events = removeInternalEvent(event->model);

            //Create a new internal event. It may or may not create the same event that was just deleted
            Time *modelAdvance = event->model->timeAdvance();
            Time *eventTime = new Time(event->time->realTime + modelAdvance->realTime, 0);
            Event<IN, OUT> *e = new Event<IN, OUT>(event->model, eventTime, "internal", event->modelName, "");
            generatedEvents.push_back(e);
        }

        return generatedEvents;
    }

    /**
     * creates confluent events, and adds it to list, and also deletes the required internal and external events
     */
    EventQueue<IN, OUT> *createConfluentEvent()
    {

        EventQueue<IN, OUT> *updatedEvents = new EventQueue<IN, OUT>(events->getNumberOfElements());

        Time * t = events->peek()->time;
        Event<IN, OUT> * current = events->remove();
        Event<IN, OUT> * eventAfter = events->peek();

        while (current != NULL)
        {

            if (eventAfter  != NULL)
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
                        if ((current->action.compare("internal") == 0 && eventAfter->action.compare("external") == 0) || (current->action.compare("external") == 0 && eventAfter->action.compare("internal") == 0))
                        {
                            events->remove(); //this will remove the eventAfter

                            string input = current->input.compare("") == 0 ? eventAfter->input : current->input;
                            Event<IN, OUT> * con = new Event<IN, OUT>(current->model, current->time, "confluent", current->modelName, input);
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

    EventQueue<IN, OUT> *removeInternalEvent(modelPtr m)
    {
        EventQueue<IN, OUT> *validEvents = new EventQueue<IN, OUT>(events->getNumberOfElements());
        Event<IN, OUT> *e = events->remove();

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
        return "Network -- events:" + to_string(events->getNumberOfElements());
    }
};

#endif // NETWORK
