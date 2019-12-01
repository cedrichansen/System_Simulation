#ifndef MODEL
#define MODEL

#include "Network.cpp"
#include "Time.cpp"
#include "Port.cpp"


template <class IN, class OUT>
class Model
{
public:

    Port<IN> * in;
    Port<OUT> * out;

    int numberOfInputs;
    Time * lastKnownTime;
    int numberOfPartsToProcess;


    virtual std::string lambda() = 0;


    virtual void externalTransition(Time elapsedTime, std::string in) = 0;


    virtual void internalTransition() = 0;


    virtual void confluentTransition(Time elapsedTime, std::string in) = 0;


    virtual Time * timeAdvance() = 0;


    virtual double getMaxTimeAdvance() = 0;

    virtual std::string toString() = 0;
};

#endif