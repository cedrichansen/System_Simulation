#include <iostream>
#include <vector>
#include "VendingMachine.cpp"
#include "Framework.cpp"

int main()
{
    VendingMachine *vm = new VendingMachine(0, 0, 0);

    int numInputs = 19;

    std::string **inputs = new std::string *[19];
    inputs[0][0] = "0.3";
    inputs[0][1] = "q";
    inputs[1][0] = "2.3";
    inputs[1][1] = "q";
    inputs[2][0] = "2.8";
    inputs[2][1] = "q";
    inputs[3][0] = "3.0";
    inputs[3][1] = "q";
    inputs[4][0] = "3.1";
    inputs[4][1] = "q";
    inputs[5][0] = "3.2";
    inputs[5][1] = "q";
    inputs[6][0] = "3.3";
    inputs[6][1] = "q";
    inputs[7][0] = "10.0";
    inputs[7][1] = "q";
    inputs[8][0] = "11.0";
    inputs[8][1] = "q";
    inputs[9][0] = "12.0";
    inputs[9][1] = "q";
    inputs[10][0] = "13.0";
    inputs[10][1] = "q";
    inputs[11][0] = "13.1";
    inputs[11][1] = "q";
    inputs[12][0] = "13.2";
    inputs[12][1] = "q";
    inputs[13][0] = "13.3";
    inputs[13][1] = "q";
    inputs[14][0] = "13.4";
    inputs[14][1] = "q";
    inputs[15][0] = "13.5";
    inputs[15][1] = "d";
    inputs[16][0] = "15.5";
    inputs[16][1] = "q";
    inputs[17][0] = "16.0";
    inputs[17][1] = "n";
    inputs[18][0] = "18.0";
    inputs[18][1] = "q";

    Framework *f = new Framework(inputs, 19, vm);
    f->start();

    delete f;
    return 0;
}
