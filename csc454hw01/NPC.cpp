#ifndef _NPC
#define _NPC

#include <cstdio> //for printf
#include "Creature.cpp"

using namespace std;

class NPC : public Creature
{
public:
    NPC(int t, int c, int l, int r)
    {
        type = t;
        creatureNumber = c;
        location = l;
        respect = r;
    }

    string toString()
    {
        return "NPC, creatureNum: " + std::to_string(creatureNumber) + "\n";
    }

    bool happyWithAction(string action, Creature *pcPtr, bool creatureIsInSameRoomAsPC, int creatureWhoDidAction)
    {
        int multiplier = 1;
        if (creatureIsInSameRoomAsPC && (creatureWhoDidAction == creatureNumber))
        {
            multiplier = 3;
        }

        //NPC like dirty or half dirty rooms
        if (action == "clean")
        {
            pcPtr->respect = pcPtr->respect - multiplier;
            printf("Creature %d grumbles. Respect is now %d\n", creatureNumber, pcPtr->respect);
            return false;
        }
        else if (action == "dirty")
        {
            pcPtr->respect = pcPtr->respect + multiplier;
            printf("Creature %d smiles. Respect is now %d\n", creatureNumber, pcPtr->respect);
            return true;
        }
        else
        {
            printf("A bad action was passed in....");
            return true;
        }
    }

    bool likeRoomState(int state)
    {
        if (state == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
};

#endif