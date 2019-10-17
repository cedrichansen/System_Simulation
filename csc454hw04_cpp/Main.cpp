#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>
#include "Model.cpp"
#include "XORModel.cpp"
#include "MemoryModel.cpp"
#include "Pipe.cpp"
#include "Port.cpp"
#include "NetworkModel.cpp"

using namespace std;

int main(int argc, char **argv)
{
    bool verbose = false;
    if (argc != 1)
    {
        string val = argv[1];
        if (val.compare("-v") == 0)
        {
            printf("verbose mode enabled\n");
            verbose = true;
        }
        else
        {
            printf("Unknown command line option");
        }
    }

    /** Free all objects placed on heap below (bottom of main)*/
    int initialState[] = {0};
    Port *xor1_In1 = new Port();
    Port *xor1_In2 = new Port();
    Port *xor1_out = new Port();
    XORModel *xor1 = new XORModel(initialState, xor1_In1, xor1_In2, xor1_out);

    Port *xor2_In1 = new Port();
    Port *xor2_In2 = new Port();
    Port *xor2_out = new Port();
    XORModel *xor2 = new XORModel(initialState, xor2_In1, xor2_In2, xor2_out);

    int mmInitial[] = {0, 0};
    Port *mmIn = new Port();
    Port *mmOut = new Port();
    MemoryModel *mm = new MemoryModel(mmInitial, mmIn, mmOut);

    NetworkModel *network = new NetworkModel(xor1, xor2, 3);

    Pipe * p1 = new Pipe(xor1_out, xor2_In1); 
    network->addPipe("p1", p1);
    Pipe * p2 = new Pipe(mmOut, xor2_In2);
    network->addPipe("p2", p2);
    Pipe * p3 = new Pipe(xor2_out, mmIn);
    network->addPipe("p3", p3);

    network->addModel("xor1", xor1);
    network->addModel("mm", mm);
    network->addModel("xor2", xor2);

    if (verbose)
    {
        map<string, Model *>::iterator itr;
        for (itr = network->children->begin(); itr != network->children->end(); itr++)
        {
            Model *current = itr->second;
            current->verbose = true;
        }
        network->verbose = true;
    }

    printf("\nOn each tick, enter  \"0/1 0/1\" for an interactive input, or '-1 -1' to exit \n");
    int a = 0;
    int b = 0;

    do
    {

        scanf("%d %d", &a, &b);
        if ((a == 0 || a == 1) && (b == 0 || b == 1))
        {
            int in[] = {a, b};
            network->tick(in);
            printf("%d\n", network->outPort->currentValue);
        }
        else if (a == -1 && b == -1)
        {
            //silently die
        }
        else
        {
            printf("Invalid input was entered\n");
        }

    } while (a != -1 && b != -1);

    network->~NetworkModel();
    xor1->~XORModel();
    xor2->~XORModel();
    mm->~MemoryModel();
    delete p1;
    delete p2;
    delete p3;
    delete xor1_In1;
    delete xor1_In2;
    delete xor1_out;
    delete xor2_In1;
    delete xor2_In2;
    delete xor2_out;
    delete mmIn;
    delete mmOut;

    return 0;
}
