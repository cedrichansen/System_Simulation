#ifndef VENDING_MACHINE
#define VENDING_MACHINE

#include "Model.cpp"
#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>
#include <float.h>
#include "Time.cpp"

#define QUARTER_VALUE 25
#define NICKEL_VALUE 5
#define COFFEE_PRICE 100
#define DIME_VALUE 10
#define TIME_ADVANCE 2

using namespace std;

class VendingMachine : public Model
{
public:
    int quarters;
    int dimes;
    int nickels;
    int customerValue;

public:
    VendingMachine(int q, int n, int d)
    {
        quarters = q;
        nickels = n;
        dimes = d;
        customerValue = 0;
    }

    std::string lambda()
    {
        string ret = "";
        int coffees = numCoffeesToDispense();
        for (int i = 0; i < coffees; i++)
        {
            ret += "COFFEE, ";
        }
        int change = amountOfChangeToGive(coffees);

        int * coinsToGive = getChange(change);
        ret += getChangeCoinsStr(coinsToGive[0], coinsToGive[1], coinsToGive[2]);
        if (getChangeCoinsStr(coinsToGive[0], coinsToGive[1], coinsToGive[2]).length() == 0)
        {
            ret = ret.substr(0, ret.length() - 2);
        }

        delete coinsToGive;

        return "{" + ret + "}";
    }

    string getChangeCoinsStr(int nickels, int dimes, int quarters)
    {
        string coinsStr = "";
        for (int i = 0; i < nickels; i++)
        {
            coinsStr += "n, ";
        }
        for (int i = 0; i < dimes; i++)
        {
            coinsStr += "d, ";
        }
        for (int i = 0; i < quarters; i++)
        {
            coinsStr += "q, ";
        }

        if (coinsStr.length() != 0)
        {
            coinsStr = coinsStr.substr(0, coinsStr.length() - 2);
        }
        return coinsStr;
    }

    void externalTransition(std::string input)
    {
        if (input.compare("q") == 0)
        {
            quarters++;
            customerValue += QUARTER_VALUE;
        }
        else if (input.compare("d") == 0)
        {
            dimes++;
            customerValue += DIME_VALUE;
        }
        else if (input.compare("n") == 0)
        {
            nickels++;
            customerValue += NICKEL_VALUE;
        }
        else
        {
            printf("Illegal character \"%s\" was entered...", input.c_str());
        }
    }

    void internalTransition()
    {
        int coffees = numCoffeesToDispense();
        int change = amountOfChangeToGive(coffees);
        int *coinsToGive = getChange(change);
        customerValue -= coffees * COFFEE_PRICE;
        customerValue -= NICKEL_VALUE * coinsToGive[0];
        customerValue -= DIME_VALUE * coinsToGive[1];
        customerValue -= QUARTER_VALUE * coinsToGive[2];
        nickels -= coinsToGive[0];
        dimes -= coinsToGive[1];
        quarters -= coinsToGive[2];
        delete coinsToGive;
    }

    int numCoffeesToDispense()
    {
        return (int)((customerValue) / COFFEE_PRICE);
    }

    int amountOfChangeToGive(int coffeesDispensed)
    {
        return (customerValue - (COFFEE_PRICE * coffeesDispensed));
    }

    int *getChange(int changeToDispense)
    {

        float amountLeftToDispense = changeToDispense;
        int numberOfQuartersToDispense = 0;
        int numberOfDimesToDispense = 0;
        int numberOfNickelsToDispense = 0;

        if (amountLeftToDispense >= QUARTER_VALUE)
        {
            numberOfQuartersToDispense = (int)(amountLeftToDispense / QUARTER_VALUE);
            if (numberOfQuartersToDispense > quarters)
            {   // we do not have enough quarters left to give
                // quarters, so give out as many as possible
                numberOfQuartersToDispense = quarters;
            }
            amountLeftToDispense -= (QUARTER_VALUE * numberOfQuartersToDispense);
        }

        if (amountLeftToDispense >= DIME_VALUE)
        {
            numberOfDimesToDispense = (int)(amountLeftToDispense / DIME_VALUE);
            if (numberOfDimesToDispense > dimes)
            {
                numberOfDimesToDispense = dimes;
            }
            amountLeftToDispense -= (DIME_VALUE * numberOfDimesToDispense);
        }

        if (amountLeftToDispense >= NICKEL_VALUE)
        {
            numberOfNickelsToDispense = (int)(amountLeftToDispense / NICKEL_VALUE);
            if (numberOfNickelsToDispense > nickels)
            {
                numberOfNickelsToDispense = nickels;
            }
            amountLeftToDispense -= (NICKEL_VALUE * numberOfNickelsToDispense);
        }

        if (amountLeftToDispense != 0)
        {
            printf("We do not have the correct combination of coins to dispense proper change :( Vending Machine is now out of order");
            exit(EXIT_FAILURE);
        }

        int *change = new int[3];
        change[0] = numberOfNickelsToDispense;
        change[1] = numberOfDimesToDispense;
        change[2] = numberOfQuartersToDispense;
        return change;
    }

    void confluentTransition(std::string input)
    {
        internalTransition();
        externalTransition(input);
    }

    /** must delete the time when it gets returned */
    Time * timeAdvance()
    {
        if (customerValue > 0) {
            return new Time(TIME_ADVANCE, 0);
        } else {
            return new Time(DBL_MAX, 0);
        } 
    }

    double getMaxTimeAdvance()
    {
        return DBL_MAX;
    }

    void print()
    {
        printf("Balance: $ %.2f\n\n", (float)customerValue / (float)100);
    }

    ~VendingMachine (){
        //delete whatever.... right now, nothing
    }
};

#endif