#include <iostream>
#include <vector>
#include "VendingMachine.cpp"
#include "Framework.cpp"

int main()
{
    VendingMachine *vm = new VendingMachine(0, 0, 0);

    int numInputs = 19;

    std::string inputs []= {
        "0.3,q",
        "2.3,q",
        "2.8,q",
        "3.0,q",
        "3.1,q",
        "3.2,q",
        "3.3,q",
        "10.0,q",
        "11.0,q",
        "12.0,q",
        "13.0,q",
        "13.1,q",
        "13.2,q",
        "13.3,q",
        "13.4,q",
        "13.5,d",
        "15.5,q",
        "16.0,n",
        "18.0,q"};

    Framework *f = new Framework(inputs, 19, vm);
    f->start();

    delete f;
    delete inputs;
    return 0;
}
