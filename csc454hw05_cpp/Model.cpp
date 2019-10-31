#ifndef MODEL
#define MODEL

#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>
#include "Time.cpp"

class Model
{

public:
    virtual std::string lambda() = 0;
    virtual void externalTransition(std::string input) = 0;
    virtual void internalTransition() = 0;
    virtual void confluentTransition(std::string input) = 0 ;
    virtual Time *timeAdvance() = 0;
    virtual double getMaxTimeAdvance() = 0;
    virtual std::string toString() {};
    virtual ~Model() {}
};


#endif