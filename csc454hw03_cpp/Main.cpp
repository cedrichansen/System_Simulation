#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>

#include "VendingMachine.cpp"

using namespace std;

int main(int argc, char **argv)
{

    int numOfNickels, numOfDimes, numOfQuarters;

    printf("Welcome to the vending machine simulator! \n");
    printf("Please enter the number of nickels, dimes and quarters to start with in the vending machine.\nFormat: \"n d q\" (Separated by spaces)\n");
    scanf("%d %d %d", &numOfNickels, &numOfDimes, &numOfQuarters);
    printf("Available commands are \"n\", for a nickel, \"d\" for a dime, \"q\" for a quarter, \"w\", to wait, and \"c\" to request change. \nInvalid characters will be ignored\nMultiple valid characters may be entered simulataneously, eg. dddnnn for 3 nikels, and 3 dimes\n");

    VendingMachine *vm = new VendingMachine(numOfNickels, numOfNickels, numOfQuarters);

    vm->startSimulation();

    delete vm;

    return 0;
}

