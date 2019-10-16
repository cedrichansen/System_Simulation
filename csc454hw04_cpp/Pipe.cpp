#ifndef PIPE
#define PIPE

#include "Port.cpp"
#include <cstdio>   //for printf

class Pipe
{
public:
    Port *sending;
    Port *receiving;

    Pipe(Port *outgoingPort, Port *receivingPort)
    {
        sending = outgoingPort;
        receiving = receivingPort;
    }

    void passValue(){
        //printf("receiving value %d Sending value %d",receiving->currentValue, sending->currentValue);
        receiving->currentValue = sending->currentValue;
        //printf("---- Post pass| receiving value %d Sending value %d\n",receiving->currentValue, sending->currentValue);

    }
};
#endif