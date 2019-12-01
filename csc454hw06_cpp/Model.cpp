#ifndef MODEL
#define MODEL

#include "Time.cpp"
#include "Port.cpp"
#include <math.h>
#include <float.h>

template <class IN, class OUT>
class Model
{
public:
    Port<IN> * * in;
    Port<OUT> * out;

    std::string name;

    int numberOfInputs;
    Time * lastKnownTime;
    int numberOfPartsToProcess;

    std::string lambda() {}

    void externalTransition(Time * elapsedTime, std::string in) {}

    void internalTransition() {}

    void confluentTransition(Time * elapsedTime, std::string in) {}

    Time *timeAdvance() {}

    double getMaxTimeAdvance() {}

    std::string toString() {}
};

#endif