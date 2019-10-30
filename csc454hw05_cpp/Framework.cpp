#ifndef FRAMEWORK
#define FRAMEWORK

#include <iostream>
#include <vector>
#include "Model.cpp"
#include "Time.cpp"

class Framework
{
public:
    std::string ** inputTrajectory;
    Model *model;
    int discreteTime;
    Time *MIN_INCREMENT;
    int numInputs;

public:
    Framework(std::string ** trajectory, int inputs, Model *m)
    {
        inputTrajectory = trajectory;
        model = m;
        numInputs = inputs;
        MIN_INCREMENT = new Time(0, 1);
    }

    void start()
    {
        

    }

    ~Framework()
    {
        //TODO: DELETE INPUT TRAJECTORY
        delete model;
        delete MIN_INCREMENT;
    }
};

#endif