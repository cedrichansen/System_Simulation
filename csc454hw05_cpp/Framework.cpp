#ifndef FRAMEWORK
#define FRAMEWORK

#include <iostream>
#include <vector>
#include "Model.cpp"
#include "Time.cpp"
#include <cstdio>   //for printf
#include <string>
#include <thread>
#include <chrono>
#include <iostream>
#include <math.h> 

class Framework
{
public:
    std::string * inputTrajectory;
    Model *model;
    int discreteTime;
    Time *MIN_INCREMENT;
    int numInputs;

public:
    Framework(std::string * trajectory, int inputs, Model *m)
    {
        inputTrajectory = trajectory;
        model = m;
        numInputs = inputs;
        MIN_INCREMENT = new Time(0, 1);
        discreteTime = 0;
    }

    void start()
    {
        printf( "Welcome to vending machine simulator xTreme 9000\nOutput/Input will automatically be processed as per the specified input trajectory\n");

        Time * timeSincePreviousEvent;
        Time * sleepTimer;
        Time * timeElapsed = new Time(0,0);
        Time * inputTime;
        Time * timeAdvance;

        for (int i = 0; i < numInputs; i++) {

            std::string currentStep =  inputTrajectory[i];
            std::string coin = currentStep.substr(currentStep.find(",") + 1, currentStep.length());
            double time = roundDouble(std::stod(currentStep.substr(0, currentStep.find(","))));

            timeAdvance = model->timeAdvance(); 
            
            inputTime = new Time(time, 0); //real input events are always 0 on discrete axis
            timeSincePreviousEvent = inputTime->since(timeElapsed);

            if (timeAdvance->realTime > timeSincePreviousEvent->realTime) {
                sleepTimer = timeSincePreviousEvent;
            } else {
                sleepTimer = timeAdvance;
            }

            std::this_thread::sleep_for(std::chrono::milliseconds((int)(sleepTimer->realTime * 1000)));

            timeElapsed = timeElapsed->timeAdvance(sleepTimer);

            processStep(coin, timeSincePreviousEvent, timeAdvance, timeElapsed);

            //since the internal transition happened, we still need to process the current input
            if (sleepTimer->realTime != timeSincePreviousEvent->realTime) {
                timeAdvance = model->timeAdvance();
                timeSincePreviousEvent = inputTime->since(timeElapsed);
                if (timeAdvance->realTime > timeSincePreviousEvent->realTime) {
                    sleepTimer = timeSincePreviousEvent;
                } else {
                    sleepTimer = timeAdvance;
                }

                std::this_thread::sleep_for(std::chrono::milliseconds((int)(sleepTimer->realTime * 1000)));
                timeElapsed = timeElapsed->timeAdvance(sleepTimer);

                processStep(coin, timeSincePreviousEvent, timeAdvance, timeElapsed);
            }
        }

        //let the internal clock expire if needed
        if (model->timeAdvance()->realTime != model->getMaxTimeAdvance()) {
            std::this_thread::sleep_for(std::chrono::milliseconds((int)(model->timeAdvance()->realTime) * 1000));
            timeElapsed = timeElapsed->timeAdvance(model->timeAdvance());
            printf("internal\n");
            printf("Real Time: %f Output %s\n", timeElapsed->realTime, model->lambda().c_str());
            model->internalTransition();
            printf("%s\n", model->toString().c_str());
        }

        delete timeSincePreviousEvent;
        delete sleepTimer;
        delete timeElapsed;
        delete inputTime;
        delete timeAdvance;

    }

       void processStep(std::string coin, Time * timeSinceLastInput, Time * timeAdvance, Time *realTime) {

                if (timeSinceLastInput->greaterThan(timeAdvance)) {
                // execute internal transition
                printf("internal\n");
                printf("Real Time: %f\n", realTime->realTime); 
                printf("Output: %s\n" , model->lambda().c_str());
                model->internalTransition();

            } else if (timeAdvance->greaterThan(timeSinceLastInput)) {
                // execute external transition
                printf("external");
                printf("Real Time: %f\nInput: %s", realTime->realTime, coin.c_str());
                model->externalTransition(coin);

            } else {
                // They must be equal! confluent case
                printf("confluent");
                printf("Real Time: %f Input: %s",realTime->realTime, coin.c_str());
                printf("Output: %s", model->lambda().c_str());
                model->confluentTransition(coin);
            }
            printf("%s\n", model->toString().c_str());

            timeAdvance = model->timeAdvance();

            while (timeAdvance->realTime == 0) {
                // Whatever model needs to do if timeadvance is 0 --- does not happen in this current project
                timeAdvance = timeAdvance->timeAdvance(MIN_INCREMENT); //advance by minimum increment instead
                printf("Time: %s" , timeAdvance->toString().c_str());
                printf("Output: %s", model->lambda().c_str());
                model->internalTransition();
                timeAdvance = model->timeAdvance();
            }
    }

    /** Rounds v to nearest hundredth*/
    double roundDouble(double v) {
        return round(v * 10000.0) / 10000.0;
    }

    ~Framework()
    {
        //TODO: DELETE INPUT TRAJECTORY
        delete model;
        delete MIN_INCREMENT;
    }
};

#endif