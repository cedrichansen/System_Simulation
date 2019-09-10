#ifndef CREATURE
#define CREATURE

#include <cstdio> //for printf
#include <string>

using namespace std;

class Creature
{

public:
    int type;
    int location; //note that location -1 corresponds to the creature having left the game
    int creatureNumber;
    int respect;
    
public:
    virtual string toString()
    {
        return "Something is wrong... A creature was instantiated\n";
    }

    // //true means happy, false mean not happy and need to change room
    virtual bool happyWithAction(string action, Creature *pcPtr, bool creatureIsInSameRoomAsPC, int creatureWhoCleanedRoom)
    {

        printf("This should never be called");
        return true;
    }

    virtual bool likeRoomState(int state)
    {
        printf("This should never be called...");
        return true;
    }

};

#endif