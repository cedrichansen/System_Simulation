#ifndef _PC
#define _PC

#include <cstdio> //for printf
#include "Creature.cpp"

using namespace std;

class PC : public Creature
{

public:
    PC(int t, int c, int l, int r){
        type = t;
        creatureNumber = c;
        location = l;
        respect = r;
    }

    string toString()
    {
        return "PC, respect: " + std::to_string(respect) + "\n";
    }

    bool happyWithAction(string action, Creature *pcPtr, bool creatureIsInSameRoomAsPC, int creatureWhoDidAction)
    {
        //PC is always happy
        return "PC - No reaction";
    }

    bool likeRoomState(int state)
    {
        //PC likes every room
        return true;
    }
};

#endif