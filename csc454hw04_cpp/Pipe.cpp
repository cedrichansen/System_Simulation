#ifndef PIPE
#define PIPE

#include "Port.cpp"

class Pipe
{
public:
    Port sending;
    Port receiving;

    Pipe(Port outgoingPort, Port receivingPort)
    {
        sending = outgoingPort;
        receiving = receivingPort;
    }

    void passValue(){
        receiving.currentValue = sending.currentValue;
    }
};
#endif