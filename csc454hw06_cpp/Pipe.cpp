#ifndef PIPE
#define PIPE

template <class T>
class Pipe
{
public:
    Port<T> *sending;
    Port<T> *receiving;

    Pipe(Port<T> *outgoingPort, Port<T> *receivingPort)
    {
        sending = outgoingPort;
        receiving = receivingPort;
    }

    /**
     * Move the value from the sendingPort to the receiving port.
     */
    void passValue()
    {
        receiving->currentValue = sending->currentValue;
    }
};

#endif