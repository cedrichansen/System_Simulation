
#ifndef TRAJ
#define TRAJ

#include <string>

class Trajectory
{
public:
    double time;
    std::string *inputs;
    int numberOfInputs;

    Trajectory(double realTime, std::string *ins, int numIns)
    {
        time = realTime;
        inputs = ins;
        numberOfInputs = numIns;
    }
};

#endif
