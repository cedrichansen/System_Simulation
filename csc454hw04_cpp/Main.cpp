#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>
#include "Model.cpp"
#include "XORModel.cpp"
#include "MemoryModel.cpp"
#include "Pipe.cpp"
#include "Port.cpp"


using namespace std;

int main(int argc, char **argv)
{
    bool verbose = false;
    if (argc != 1) {
        string val = argv[1];
        if (val.compare("-v") == 0) {
            printf("verbose mode enabled -- TODO SET VERBOSE MODE");
            verbose = true;
        } else {
            printf("Unknown command line option");
        }
    } 

    //if verbose, loop through all of the models and set the debug to true

    



    return 0;
}
