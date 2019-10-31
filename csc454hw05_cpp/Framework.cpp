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

        Time * timeSincePreviousEvent = new Time(0,0);
        Time * sleepTimer = new Time(0,0);
        Time * timeElapsed = new Time(0,0);
        Time * inputTime = new Time(0,0);
        Time * timeAdvance = new Time(0,0);

        for (int i = 0; i < numInputs; i++) {

            std::string currentStep =  inputTrajectory[i];
            std::string coin = currentStep.substr(currentStep.find(",") + 1, currentStep.length());
            double time = roundDouble(std::stod(currentStep.substr(0, currentStep.find(","))));
            
            delete timeAdvance;
            timeAdvance = model->timeAdvance(); 
            
            inputTime->changeTime(time, 0);

            delete timeSincePreviousEvent;
            timeSincePreviousEvent = inputTime->since(timeElapsed);

            delete sleepTimer;
            if (timeAdvance->realTime > timeSincePreviousEvent->realTime) {
                sleepTimer = new Time(timeSincePreviousEvent->realTime, timeSincePreviousEvent->discreteTime);
            } else {
                sleepTimer = new Time(timeAdvance->realTime, timeAdvance->discreteTime);
            }

            std::this_thread::sleep_for(std::chrono::milliseconds((int)(sleepTimer->realTime * 1000)));

            timeElapsed->timeAdvance(sleepTimer);

            processStep(coin, timeSincePreviousEvent, timeAdvance, timeElapsed);

            //since the internal transition happened, we still need to process the current input
            if (sleepTimer->realTime != timeSincePreviousEvent->realTime) {
                delete timeAdvance;
                timeAdvance = model->timeAdvance();
                delete timeSincePreviousEvent;
                timeSincePreviousEvent = inputTime->since(timeElapsed);
                delete sleepTimer;
                if (timeAdvance->realTime > timeSincePreviousEvent->realTime) {
                    sleepTimer = new Time(timeSincePreviousEvent->realTime, timeSincePreviousEvent->discreteTime);
                } else {
                    sleepTimer = new Time(timeAdvance->realTime, timeAdvance->discreteTime);
                }

                std::this_thread::sleep_for(std::chrono::milliseconds((int)(sleepTimer->realTime * 1000)));
                timeElapsed->timeAdvance(sleepTimer);

                processStep(coin, timeSincePreviousEvent, timeAdvance, timeElapsed);
            }
        }

        delete timeAdvance;
        timeAdvance = model->timeAdvance();
        //let the internal clock expire if needed
        if (timeAdvance->realTime != model->getMaxTimeAdvance()) {
            std::this_thread::sleep_for(std::chrono::milliseconds((int)(timeAdvance->realTime) * 1000));
            timeElapsed->timeAdvance(timeAdvance);
            printf("internal\n");
            printf("Real Time: %.2f Output %s\n", timeElapsed->realTime, model->lambda().c_str());
            model->internalTransition();
            model->print();
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
                printf("Real Time: %.2f\n", realTime->realTime); 
                printf("Output: %s\n" , model->lambda().c_str());
                model->internalTransition();

            } else if (timeAdvance->greaterThan(timeSinceLastInput)) {
                // execute external transition
                printf("external\n");
                printf("Real Time: %.2f Input: %s\n", realTime->realTime, coin.c_str());
                model->externalTransition(coin);

            } else {
                // They must be equal! confluent case
                printf("confluent\n");
                printf("Real Time: %.2f Input: %s\n",realTime->realTime, coin.c_str());
                printf("Output: %s\n", model->lambda().c_str());
                model->confluentTransition(coin);
            }
            model->print();
            
            // delete timeAdvance;
            // timeAdvance = model->timeAdvance();

            // while (timeAdvance->realTime == 0) {
            //     // Whatever model needs to do if timeadvance is 0 --- does not happen in this current project
            //     timeAdvance->timeAdvance(MIN_INCREMENT); //advance by minimum increment instead
            //     printf("Time: %s\n" , timeAdvance->toString().c_str());
            //     printf("Output: %s\n", model->lambda().c_str());
            //     model->internalTransition();
            //     delete timeAdvance;
            //     timeAdvance = model->timeAdvance();
            // }
            // delete timeAdvance;
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