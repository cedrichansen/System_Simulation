#ifndef ANIMAL
#define ANIMAL

#include <cstdio> //for printf
#include "Creature.cpp"

using namespace std;

class Animal : public Creature
{
public:
    Animal(int t, int c, int l, int r)
    {
        type = t;
        creatureNumber = c;
        location = l;
        respect = r;
    }
    string toString()
    {
        return "Animal, creatureNum: " + std::to_string(creatureNumber) + "\n";
    }

    bool happyWithAction(string action, Creature *pcPtr, bool creatureIsInSameRoomAsPC, int creatureWhoDidAction)
    {
        int multiplier = 1;
        if (creatureIsInSameRoomAsPC && (creatureWhoDidAction == creatureNumber))
        {
            multiplier = 3;
        }

        //animals like clean or half dirty rooms
        if (action == "clean")
        {
            pcPtr->respect = pcPtr->respect + multiplier;
            printf("Creature %d licks face. Respect is now %d\n", creatureNumber, pcPtr->respect);
            return true;
        }
        else if (action == "dirty")
        {
            pcPtr->respect = pcPtr->respect - multiplier;
            printf("Creature %d growls. Respect is now %d\n", creatureNumber, pcPtr->respect);
            return false;
        }
        else
        {
            printf("A bad action was passed in....");
            return true;
        }
    }

    bool likeRoomState(int state)
    {
        if (state == 2)
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