#ifndef TIME
#define TIME

#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>
#include <math.h> 
class Time {
    public:
     double realTime;
     int discreteTime;

    Time (double r, int d) {
        realTime = r;
        discreteTime = d;
    }
    Time(){
        realTime = 0;
        discreteTime = 0;
    }

    bool equals(Time * o) {
        return realTime == o->realTime;
    }

    int hashCode() {
        return 51 ^ (int)realTime >> 31 * discreteTime;
    }

    Time * timeAdvance(Time * advanceBy) {
        if (equals(advanceBy)) {
            return new Time(realTime, discreteTime + advanceBy->discreteTime);
        } else {
            return new Time(round(realTime + advanceBy->realTime), 0);
        }
    }


    Time * since(Time * older) {
        return new Time(roundDouble(realTime- older->realTime), discreteTime - older->discreteTime);
    }

    /** Rounds v to nearest hundredth*/
    double roundDouble(double v) {
        return round(v * 10000.0) / 10000.0;
    }

    int compareTo(Time * o) {
        if (o->realTime > realTime) {
            return -1;
        } else if (o->realTime < realTime) {
            return 1;
        } else {
            //times are equal 
            return (discreteTime - o->discreteTime);
        }
    }

    bool greaterThan(Time * other) {
        return compareTo(other) >= 1;
    }

    std::string toString(){
        return "(" + std::to_string(realTime) + "," + std::to_string(discreteTime) + ")";
    }

   

};

#endif