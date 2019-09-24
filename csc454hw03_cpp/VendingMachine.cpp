#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>

using namespace std;

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

    void dispenseCoffee()
    {
        int numberOfCoffees = (int)((customerValue) / COFFEE_PRICE);

        if (numberOfCoffees > 0)
        {
            customerValue -= numberOfCoffees * COFFEE_PRICE;
            printf("Dispensed %d coffee(s)! Balance is now: %.2f\n", numberOfCoffees, (customerValue / 100));
        }
    }

    void getChange(bool isForOutput)
    {
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
                { 
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
                //we do not have enough coins to dispense correct amount of change
                printf("EXCEPTION THROW HERE");
                
            }

            if (!isForOutput)
            {
                // The output function cannot modify the state, so we hold on to the state so that the state transition function can make the state change
                numberOfNickels -= numberOfNickelsToDispense;
                numberOfDimes -= numberOfDimesToDispense;
                numberOfQuarters -= numberOfQuartersToDispense;

                customerValue -= amountDispensed;
                changeButtonPressed = false;
            }
            else
            {
                // The output function will be printing out values, aka, producing output
                if (amountDispensed != 0)
                {
                    printf("Change has been returned: %d Quarters, %d Dimes, %d Nickels\nTotal Value returned: %.2f", numberOfQuartersToDispense, numberOfDimesToDispense, numberOfNickelsToDispense, (calculateValue(numberOfNickelsToDispense, numberOfDimesToDispense, numberOfQuartersToDispense) / 100));
                }
                else
                {
                    printf("Change button was pressed, but there is no balance to dispense\n");
                }
            }
        }
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
        dispenseCoffee();
        getChange(true);

        adjustState();

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
                printf("%s is an invalid token and will be ignored\n", string(1,c).c_str());
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

    void adjustState()
    {
        getChange(false);
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
};