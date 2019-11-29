#ifndef MODEL
#define MODEL

template <class IN, class OUT>
class Model
{
public:
    Network parent;

    Port<IN> * in;
    Port<OUT> out;

    int numberOfInputs;
    Time * lastKnownTime;
    int numberOfPartsToProcess;


    void addParent(Network n)
    {
        parent = n;
    }


    std::string lambda() = 0;


    void externalTransition(Time elapsedTime, String in) = 0;


    void internalTransition() = 0;


    void confluentTransition(Time elapsedTime, String in) = 0;


    Time timeAdvance() = 0;


    double getMaxTimeAdvance() = 0;

    std::string toString() = 0;
};

#endif