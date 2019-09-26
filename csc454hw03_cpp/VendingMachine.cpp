#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>

using namespace std;

struct VendingMachineException : public exception
{
    const char *message() const throw()
    {
        return "OH NO! Looks like the vending machine does not have the right combination of coins to produce the correct change :(\nPlease contact management (613-898-2930) to refill the coins in the vending machine, AND, to receive a complimentary coffee, on us, free of charge!\nWe again apologize for this inconvenience, and we hope to be able to better service you next time!\n";
    }
};

class VendingMachine
{

public:
    // a few helpers
    static const int QUARTER_VALUE = 25;
    static const int DIME_VALUE = 10;
    static const int NICKEL_VALUE = 5;
    static const int COFFEE_PRICE = 100;
    int tikNumber;

    // 5 variables representing machine state
    int numberOfNickels;
    int numberOfDimes;
    int numberOfQuarters;
    float customerValue;
    bool changeButtonPressed;

public:
    VendingMachine(int initialNickels, int initialDimes, int initialQuarters)
    {
        numberOfNickels = initialNickels;
        numberOfDimes = initialDimes;
        numberOfQuarters = initialQuarters;
        changeButtonPressed = false;
        customerValue = 0;
        tikNumber = 0;
    }

    int dispenseCoffee()
    {
        return (int)((customerValue) / COFFEE_PRICE);
    }

    int *getChange()
    {
        int * change = new int[3];

        if (changeButtonPressed)
        {
            float amountToDispense = customerValue;
            float amountDispensed = 0;
            int numberOfQuartersToDispense = 0;
            int numberOfDimesToDispense = 0;
            int numberOfNickelsToDispense = 0;

            if (amountToDispense >= QUARTER_VALUE)
            {
                numberOfQuartersToDispense = (int)(amountToDispense / QUARTER_VALUE);
                if (numberOfQuartersToDispense > numberOfQuarters)
                { // we do not have enough quarters left to give
                    // quarters, so give out as many as possible
                    numberOfQuartersToDispense = numberOfQuarters;
                }
                amountToDispense -= (QUARTER_VALUE * numberOfQuartersToDispense);
                amountDispensed += (QUARTER_VALUE * numberOfQuartersToDispense);
            }

            if (amountToDispense >= DIME_VALUE)
            {
                numberOfDimesToDispense = (int)(amountToDispense / DIME_VALUE);
                if (numberOfDimesToDispense > numberOfDimes)
                {
                    numberOfDimesToDispense = numberOfDimes;
                }
                amountToDispense -= (DIME_VALUE * numberOfDimesToDispense);
                amountDispensed += (DIME_VALUE * numberOfDimesToDispense);
            }

            if (amountToDispense >= NICKEL_VALUE)
            {
                numberOfNickelsToDispense = (int)(amountToDispense / NICKEL_VALUE);
                if (numberOfNickelsToDispense > numberOfNickels)
                {
                    numberOfNickelsToDispense = numberOfNickels;
                }
                amountToDispense -= (NICKEL_VALUE * numberOfNickelsToDispense);
                amountDispensed += (NICKEL_VALUE * numberOfNickelsToDispense);
            }

            if (amountDispensed != customerValue)
            {
                // we do not have enough coins to dispense correct amount of change
                try
                {
                    throw VendingMachineException();
                }
                catch (VendingMachineException &vme)
                {
                    printf("%s", vme.message());
                    exit(EXIT_FAILURE);
                }
            }

            change[0] = numberOfNickelsToDispense;
            change[1] = numberOfDimesToDispense;
            change[2] = numberOfQuartersToDispense;
        }
        else
        { //no change will be dispensed
            change[0] = 0;
            change[1] = 0;
            change[2] = 0;
        }
        
        return change;

    }

    float calculateValue(int nickels, int dimes, int quarters)
    {
        return (NICKEL_VALUE * nickels) + (DIME_VALUE * dimes) + (QUARTER_VALUE * quarters);
    }

    void startSimulation()
    {

        string command = "";
        getline(cin, command);

        do
        {
            printf("\nEnter command: \n");
            getline(cin, command);
            processAction(command);

        } while (command != "exit");
    }
    void processAction(string command)
    {
        if (command == "exit")
        {
            return;
        }

        tikNumber++;

        // process output from the current state
        { // lambda
            printCoffee(dispenseCoffee());
            int *change = getChange();
            printChange(change[0], change[1], change[2]);
            delete change;
            change = NULL;
        }

        { // delta
            modifyState(dispenseCoffee());
            int *change = getChange();
            modifyState(change[0], change[1], change[2]);
            delete change;
            change = NULL;
        }

        // modify the state based on the input
        char commands[command.length() + 1];
        copy(command.begin(), command.end(), commands);
        commands[command.size()] = '\0';
        int quartersInserted = 0;
        int dimesInserted = 0;
        int nickelsInserted = 0;

        for (char c : commands)
        {
            if (c == 'w')
            {
                // do nothing
                printf("Waiting for next tik...\n");
            }
            else if (c == 'q')
            {
                // quarter inserted
                quartersInserted++;
            }
            else if (c == 'd')
            {
                // dime inserted
                dimesInserted++;
            }
            else if (c == 'n')
            {
                // nickel inserted
                nickelsInserted++;
            }
            else if (c == 'c')
            {
                printf("Change button was pressed! Change will be dispensed on next tik\n");
                changeButtonPressed = true;
            }
            else if (c != 0)
            {
                printf("%s is an invalid token and will be ignored\n", string(1, c).c_str());
            }
        }

        if (quartersInserted != 0 || nickelsInserted != 0 || dimesInserted != 0)
        {
            // at least one type of change was inserted
            addCoins(nickelsInserted, dimesInserted, quartersInserted);
            printf("Added: %d nickels, %d dimes, %d quarters\nTotal Added: %.2f \n", nickelsInserted, dimesInserted, quartersInserted, (calculateValue(nickelsInserted, dimesInserted, quartersInserted) / 100));
        }

        printVMInfo();
    }

    void addCoins(int nickels, int dimes, int quarters)
    {
        numberOfQuarters += quarters;
        numberOfDimes += dimes;
        numberOfNickels += nickels;

        customerValue += calculateValue(nickels, dimes, quarters);
    }

    void printVMInfo()
    {
        printf("---Vending Machine info---\nNickels: %d Dimes: %d Quarters: %d\nBalance: %.2f\nCurrent Tik: %d\n", numberOfNickels, numberOfDimes, numberOfQuarters, customerValue / 100, tikNumber);
    }

    void modifyState(int numberOfCoffees)
    {
        customerValue -= numberOfCoffees * COFFEE_PRICE;
    }

    void modifyState(int numberOfNickelsToDispense, int numberOfDimesToDispense,
                     int numberOfQuartersToDispense)
    {
        numberOfNickels -= numberOfNickelsToDispense;
        numberOfDimes -= numberOfDimesToDispense;
        numberOfQuarters -= numberOfQuartersToDispense;

        int amountDispensed = (NICKEL_VALUE * numberOfNickelsToDispense) + (DIME_VALUE * numberOfDimesToDispense) + (QUARTER_VALUE * numberOfQuartersToDispense);

        customerValue -= amountDispensed;
        changeButtonPressed = false;
    }

    void printChange(int numberOfNickelsToDispense, int numberOfDimesToDispense,
                     int numberOfQuartersToDispense)
    {
        if (changeButtonPressed)
        {
            if (numberOfDimesToDispense + numberOfNickelsToDispense + numberOfQuartersToDispense == 0)
            {
                printf("Change button was pressed, but there is no change to dispense!\n");
            }
            else
            {
                printf("Change has been returned: %d Quarters, %d Dimes, %d Nickels. Total value returned %.2f\n", numberOfQuartersToDispense, numberOfDimesToDispense, numberOfNickelsToDispense, (calculateValue(numberOfNickelsToDispense, numberOfDimesToDispense, numberOfQuartersToDispense) / 100));
            }
        }
    }

    void printCoffee(int numberOfCoffees)
    {
        if (numberOfCoffees != 0)
        {
            printf("Dispensed %d coffee(s)! Balance is now: %.2f\n", numberOfCoffees, ((customerValue - (numberOfCoffees * COFFEE_PRICE)) / 100));
        }
    }
};