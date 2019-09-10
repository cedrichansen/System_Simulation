#ifndef ROOM
#define ROOM

#include <cstdio> //for printf
#include "Creature.cpp"
#include <string>

using namespace std;

class Room
{

public:
    int roomNumber;
    int state;

    int northNum;
    Room *north;

    int southNum;
    Room *south;

    int eastNum;
    Room *east;

    int westNum;
    Room *west;

    int currentNumberOfCreatures;   

public:

    string getStateStr()
    {
        if (state == 0)
        {
            return "clean";
        }
        else if (state == 1)
        {
            return "half-dirty";
        }
        else if (state == 2)
        {
            return "dirty";
        }
        else
        {
            return "Error state";
        }
    }

    string getNeighboursStr()
    {
        string neighbours = "";

        if (northNum != -1)
        {
            neighbours += (std::to_string(northNum) + " to the north, ");
        }
        if (southNum != -1)
        {
            neighbours += (std::to_string(southNum) + " to the south, ");
        }
        if (eastNum != -1)
        {
            neighbours += (std::to_string(eastNum) + " to the east, ");
        }
        if (westNum != -1)
        {
            neighbours += (std::to_string(westNum) + " to the west, ");
        }

        //if somehow we only have 1 room in the whole game...
        if (neighbours == "")
        {
            neighbours += "No neighbours ";
        }

        return neighbours;
    }


    bool clean()
    {   
        bool success = false;
        if (state == 1 || state == 2)
        {
            state--;
            success =  true;
        }
        return success;

    }

    bool dirty()
    {
        bool success = false;
        if (state == 0 || state == 1)
        {
            state++;
            success = true;
        }
        return success;

    }
};

#endif