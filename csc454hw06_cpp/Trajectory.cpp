
#ifndef TRAJ
#define TRAJ

#include <string>

class Trajectory {

    double time;
    std::string * inputs;

    Trajectory(double realTime, std::string * ins) {
        time = realTime;
        inputs = ins;
    }

};

#endif
