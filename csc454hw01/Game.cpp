#ifndef GAME
#define GAME

#include <cstdio>   //for printf
#include <stdio.h>  //for scanf
#include <iostream> //for getline
#include <string>

#include "Room.cpp"
#include "Creature.cpp"
#include "PC.cpp"
#include "NPC.cpp"
#include "Animal.cpp"

using namespace std;

class Game
{

public:
    int numberOfRooms;
    Room *rooms;
    int numberOfCreatures;
    Creature **creatures;
    bool debug;

    //Creature PC;
    Creature * PCptr;

    const static int MAX_CREATURES_PER_ROOM = 10;

public:
    /*
    * Initialize the array of rooms once the number is known
    */
    void initRoomsArr()
    {
        rooms = new Room[numberOfRooms];
    }

    /*
    * Initialize the array of creatures once the number is known
    */
    void initCreaturesArr()
    {
        creatures = new Creature*[numberOfCreatures];
    }

    /*
    * Used for prints when debug flag is passed in
    */
    void debugPrint(string message)
    {
        if (debug == true)
        {
            printf("%s\n", message.c_str());
        }
    }

    /**
     * Once rooms are created, and we know which rooms connects to which other, we can link them
     */
    void linkRooms()
    {
        for (int i = 0; i < numberOfRooms; i++)
        {
            if (debug == true)
            {
                printf("\nLinking room %d\n", i);
            }

            if (rooms[i].northNum != -1)
            {
                rooms[i].north = &rooms[rooms[i].northNum];
                debugPrint("North");
            }
            if (rooms[i].southNum != -1)
            {
                rooms[i].south = &rooms[rooms[i].southNum];
                debugPrint("south");
            }
            if (rooms[i].eastNum != -1)
            {
                rooms[i].east = &rooms[rooms[i].eastNum];
                debugPrint("east");
            }
            if (rooms[i].westNum != -1)
            {
                rooms[i].west = &rooms[rooms[i].westNum];
                debugPrint("west");
            }
        }
    }

    /**
     * Once creatures are created, and we know which rooms they belong in, we can put the creatures in their rooms
     */
    void putCreaturesInRooms()
    {
        for (int i = 0; i < numberOfCreatures; i++)
        {
            if (debug)
            {
                printf("Putting creature %d in room %d\n", i, creatures[i]->location);
            }
            int roomNum = creatures[i]->location;
            rooms[roomNum].currentNumberOfCreatures++;

            if (creatures[i]->type == 0)
            {
                PCptr = creatures[i];
                PCptr->respect = 40;
            }
        }
    }

    /*
    *   Used to initialize all of the componants of the game
    */
    void initialize(bool isDebug)
    {
        debug = isDebug;

        scanf("%d", &numberOfRooms);
        if (debug)
        {
            printf("initialized %d rooms\n", numberOfRooms);
        }
        initRoomsArr();
        for (int i = 0; i < numberOfRooms; i++)
        {
            Room r;
            scanf("%d %d %d %d %d", &r.state, &r.northNum, &r.southNum, &r.eastNum, &r.westNum);
            r.roomNumber = i;
            r.currentNumberOfCreatures = 0;
            rooms[i] = r;
        }

        linkRooms();

        debugPrint("Rooms initialized... Moving on to creatures");

        //initialize creatures here
        scanf("%d", &numberOfCreatures);
        if (debug)
        {
            printf("initialized %d creatures\n", numberOfCreatures);
        }

        initCreaturesArr();

        for (int i = 0; i < numberOfCreatures; i++)
        {

            int creatureType  = 0;
            int creatureLocation = 0;
            scanf("%d %d", &creatureType, &creatureLocation);

            if (creatureType == 0) {
                creatures[i] = new PC(creatureType, i, creatureLocation, 40);

            } else if (creatureType == 1) {
                creatures[i] = new Animal(creatureType, i, creatureLocation, -1);

            } else if (creatureType ==2) {
                creatures[i] = new NPC(creatureType, i, creatureLocation, -1);
            }
            
        }

        putCreaturesInRooms();

        debugPrint("Game initialized");
    }

    void play()
    {
        string command;

        do
        {
            printf("Type new command:\n");
            getline(cin, command);

            if (command.find(':') != string::npos)
            {
                creature_action(command);
            }
            else
            {
                PC_Action(command);
            }

            if (PCptr->respect < 0)
            {
                printf("Oh no, you have lost the game... You treated all of the creatures very poorly");
            }
            else if (PCptr->respect > 80)
            {
                printf("Yay, you have won the game, because all of the creatures love you so much");
            }

        } while (command != "exit");

        printf("Goodbye\n");
    }

    string getCurrentCreaturesInRoom(int roomNum)
    {
        string creatureStr = "";
        for (int i = 0; i < numberOfCreatures; i++)
        {
            if (creatures[i]->location == roomNum)
            {
                creatureStr += creatures[i]->toString();
            }
        }

        return creatureStr;
    }

    void leaveRoom(Creature *creature)
    {
        //this function is called when a creature is upset with the state of a room;
        Room *currentRoom = &rooms[creature->location];

        bool foundNewRoom = false;

        //go into any room and change state if needed
        if (currentRoom->northNum != -1)
        {
            if (currentRoom->north->currentNumberOfCreatures < MAX_CREATURES_PER_ROOM && !foundNewRoom)
            {
                move(creature->creatureNumber, currentRoom->northNum);
                printf("Creature %d moved north to room %d\n", creature->creatureNumber, creature->location);
                checkIfCreatureLikesNewRoom(creature);
                foundNewRoom = true;
            }
        }
        if (currentRoom->southNum != -1)
        {
            if (currentRoom->south->currentNumberOfCreatures < MAX_CREATURES_PER_ROOM && !foundNewRoom)
            {
                move(creature->creatureNumber, currentRoom->southNum);
                printf("Creature %d moved south to room %d\n", creature->creatureNumber, creature->location);
                checkIfCreatureLikesNewRoom(creature);
                foundNewRoom = true;
            }
        }
        if (currentRoom->eastNum != -1)
        {
            if (currentRoom->east->currentNumberOfCreatures < MAX_CREATURES_PER_ROOM && !foundNewRoom)
            {
                move(creature->creatureNumber, currentRoom->eastNum);
                printf("Creature %d moved east to room %d\n", creature->creatureNumber, creature->location);
                checkIfCreatureLikesNewRoom(creature);
                foundNewRoom = true;
            }
        }
        if (currentRoom->westNum != -1)
        {
            if (currentRoom->south->currentNumberOfCreatures < MAX_CREATURES_PER_ROOM && !foundNewRoom)
            {
                move(creature->creatureNumber, currentRoom->westNum);
                printf("Creature %d moved west to room %d\n", creature->creatureNumber, creature->location);
                checkIfCreatureLikesNewRoom(creature);
                foundNewRoom = true;
            }
        }

        if (!foundNewRoom) //if the creature has still not found a new room, we make that creature vanish
        {
            printf("Oh no! Creature %d has no where to go, and is leaving the game!\n", creature->creatureNumber);

            //remove creature
            Room *currentRoom = &rooms[creature->location];
            currentRoom->currentNumberOfCreatures--;
            creature->location = -1;

            //make all other creatures in the room with the PC growl
            for (int i =0; i<numberOfCreatures; i++) {
                if (creatures[i]->location == PCptr->location) {
                    PCptr->respect--;
                    if(creatures[i]->type == 1) {
                        printf("Creature %d growls at human for being so inconsiderate and making a creature leave. Respect is now %d\n", creatures[i]->creatureNumber, PCptr->respect);
                    } else if (creatures[i]->type == 2) {
                       printf("Creature %d grumbles at human for being so inconsiderate and making a creature leave. Respect is now %d\n", creatures[i]->creatureNumber, PCptr->respect);
                    }
                }
            }

        }
    }

    void checkIfCreatureLikesNewRoom(Creature *creature)
    {
        if (!creature->likeRoomState(rooms[creature->location].state))
        {
            if (creature->type == 1)
            {
                rooms[creature->location].clean();
                printf("Creature %d cleaned room %d, and is now happy\n", creature->creatureNumber, creature->location);
            }
            else if (creature->type == 2)
            {
                rooms[creature->location].dirty();
                printf("Creature %d dirtied room %d, and is now happy\n", creature->creatureNumber, creature->location);
            }
        }
    }

    bool move(int creatureNumber, int destinationRoomNumber)
    {

        if (rooms[destinationRoomNumber].currentNumberOfCreatures < MAX_CREATURES_PER_ROOM)
        {
            Creature *creature = creatures[creatureNumber];

            Room *oldRoom = &rooms[creature->location];
            oldRoom->currentNumberOfCreatures--;

            Room *newRoom = &rooms[destinationRoomNumber];
            creature->location = destinationRoomNumber;
            newRoom->currentNumberOfCreatures++;
            return true;
        }
        else
        {
            printf("Cannot move into room %d! It is already full\n", destinationRoomNumber);
            if (creatureNumber != PCptr->creatureNumber)
            {
                PCptr->respect--;
            }
            if (creatures[creatureNumber]->type == 1)
            {
                printf("Creature %d growls. Respect is now %d\n", creatureNumber, PCptr->respect);
            }
            else if (creatures[creatureNumber]->type == 2)
            {
                printf("Creature %d grumbles. Respect is now %d\n", creatureNumber, PCptr->respect);
            }

            return false;
        }
    }

    void PC_Action(string command)
    {
        Room *pcCurrentRoom = &rooms[PCptr->location];

        if (debug)
        {
            printf("PC must perform %s\n", command.c_str());
        }

        if (command == "look")
        {
            printf("Room %d, %s, Neighbours: %s contains %d creatures:\n%s\n", pcCurrentRoom->roomNumber, pcCurrentRoom->getStateStr().c_str(), pcCurrentRoom->getNeighboursStr().c_str(), pcCurrentRoom->currentNumberOfCreatures, getCurrentCreaturesInRoom(pcCurrentRoom->roomNumber).c_str());
        }
        else if (command == "clean")
        {
            bool success = rooms[PCptr->location].clean();
            if (success)
            {
                for (int i = 0; i < numberOfCreatures; i++)
                {
                    if (creatures[i]->location == pcCurrentRoom->roomNumber)
                    {
                        bool happy = creatures[i]->happyWithAction("clean", PCptr, false, PCptr->creatureNumber);
                        if (!happy && (pcCurrentRoom->state == 0 || pcCurrentRoom->state == 2))
                        {
                            leaveRoom(creatures[i]);
                        }
                    }
                }
            }
            else
            {
                printf("Room is already clean!\n");
            }
        }
        else if (command == "dirty")
        {
            bool success = rooms[PCptr->location].dirty();
            if (success)
            {
                for (int i = 0; i < numberOfCreatures; i++)
                {
                    if (creatures[i]->location == pcCurrentRoom->roomNumber)
                    {
                        bool happy = creatures[i]->happyWithAction("dirty", PCptr, false, PCptr->creatureNumber);
                        if (!happy && (pcCurrentRoom->state == 0 || pcCurrentRoom->state == 2))
                        {
                            leaveRoom(creatures[i]);
                        }
                    }
                }
            }
            else
            {
                printf("Room is already dirty!\n");
            }
        }
        else if (command == "north")
        {
            if (pcCurrentRoom->northNum != -1)
            {
                bool success = move(PCptr->creatureNumber, pcCurrentRoom->northNum);
                if (success)
                {
                    printf("You leave towards the north\n");
                }
            }
            else
            {
                printf("No neighbour to the north, cannot move there\n");
            }
        }
        else if (command == "south")
        {
            if (pcCurrentRoom->southNum != -1)
            {
                bool success = move(PCptr->creatureNumber, pcCurrentRoom->southNum);
                if (success)
                {
                    printf("You leave towards the south\n");
                }
            }
            else
            {
                printf("No neighbour to the south, cannot move there\n");
            }
        }
        else if (command == "east")
        {
            if (pcCurrentRoom->eastNum != -1)
            {
                bool success = move(PCptr->creatureNumber, pcCurrentRoom->eastNum);
                if (success)
                {
                    printf("You leave towards the east\n");
                }
            }
            else
            {
                printf("No neighbour to the east, cannot move there\n");
            }
        }
        else if (command == "west")
        {
            if (pcCurrentRoom->westNum != -1)
            {
                bool success = move(PCptr->creatureNumber, pcCurrentRoom->westNum);
                if (success)
                {
                    printf("You leave towards the west\n");
                }
            }
            else
            {
                printf("No neighbour to the west, cannot move there\n");
            }
        }
        else if (command == "help")
        {
            printHelpMessage();
        }
        else if (command == "exit")
        {
            //do nothing
        }
        else
        {
            printf("Enter \'help\' for available commands\n");
        }
        printf("\n");
    }

    void creature_action(string command)
    {
        int creatureNum = stoi(command.substr(0, command.find(':')));
        string action = command.substr(command.find(':') + 1, command.size());

        Creature *creature = creatures[creatureNum];
        Room *currentRoom = &rooms[creature->location];

        if (creature->location != -1) //make sure that creature is still in the game
        {
            if (debug)
            {
                printf("Creature %d must perform %s\n", creatureNum, action.c_str());
            }
            if (action == "look")
            {
                printf("Room %d, %s, Neighbours: %s contains %d creatures:\n%s\n", currentRoom->roomNumber, currentRoom->getStateStr().c_str(), currentRoom->getNeighboursStr().c_str(), currentRoom->currentNumberOfCreatures, getCurrentCreaturesInRoom(currentRoom->roomNumber).c_str());
            }
            else if (action == "clean")
            {
                bool success = currentRoom->clean();
                if (success)
                {
                    for (int i = 0; i < numberOfCreatures; i++)
                    {
                        if (creatures[i]->location == currentRoom->roomNumber)
                        {
                            bool happy = false;
                            if (PCptr->location == currentRoom->roomNumber)
                            {
                                happy = creatures[i]->happyWithAction("clean", PCptr, true, creature->creatureNumber);
                            }
                            else
                            {
                                happy = creatures[i]->happyWithAction("clean", PCptr, false, creature->creatureNumber);
                            }
                            if (!happy && (currentRoom->state == 0 || currentRoom->state == 2))
                            {
                                leaveRoom(creatures[i]);
                            }
                        }
                    }
                }
                else
                {
                    printf("Room is already clean!\n");
                }
            }
            else if (action == "dirty")
            {
                bool success = currentRoom->dirty();
                if (success)
                {
                    for (int i = 0; i < numberOfCreatures; i++)
                    {
                        if (creatures[i]->location == currentRoom->roomNumber)
                        {
                            bool happy = false;
                            if (PCptr->location == currentRoom->roomNumber)
                            {
                                happy = creatures[i]->happyWithAction("dirty", PCptr, true, creatureNum);
                            }
                            else
                            {
                                happy = creatures[i]->happyWithAction("dirty", PCptr, false, creatureNum);
                            }
                            if (!happy && (currentRoom->state == 0 || currentRoom->state == 2))
                            {
                                leaveRoom(creatures[i]);
                            }
                        }
                    }
                }
                else
                {
                    printf("Room is already dirty!\n");
                }
            }
            else if (action == "north")
            {
                if (currentRoom->northNum != -1)
                {
                    bool success = move(creature->creatureNumber, currentRoom->northNum);
                    if (success)
                    {
                        printf("Creature %d leaves towards the north\n", creature->creatureNumber);
                        checkIfCreatureLikesNewRoom(creature);
                    }
                }
                else
                {
                    printf("No neighbour to the north, cannot move there\n");
                }
            }
            else if (action == "south")
            {
                if (currentRoom->southNum != -1)
                {
                    bool success = move(creature->creatureNumber, currentRoom->southNum);
                    if (success)
                    {
                        printf("Creature %d leaves towards the south\n", creature->creatureNumber);
                        checkIfCreatureLikesNewRoom(creature);
                    }
                }
                else
                {
                    printf("No neighbour to the south, cannot move there\n");
                }
            }
            else if (action == "east")
            {
                if (currentRoom->eastNum != -1)
                {
                    bool success = move(creature->creatureNumber, currentRoom->eastNum);
                    if (success)
                    {
                        printf("Creature %d leaves towards the east\n", creature->creatureNumber);
                        checkIfCreatureLikesNewRoom(creature);
                    }
                }
                else
                {
                    printf("No neighbour to the east, cannot move there\n");
                }
            }
            else if (action == "west")
            {
                if (currentRoom->westNum != -1)
                {
                    bool success = move(creature->creatureNumber, currentRoom->westNum);
                    if (success)
                    {
                        printf("Creature %d leaves towards the west\n", creature->creatureNumber);
                        checkIfCreatureLikesNewRoom(creature);
                    }
                }
                else
                {
                    printf("No neighbour to the west, cannot move there\n");
                }
            }
            else if (action == "help")
            {
                printHelpMessage();
            }
            else if (action == "exit")
            {
                //do nothing
            }
            else
            {
                printf("Enter \'help\' for available commands\n");
            }
        } else {
            printf("That creature is no longer in the game :(\n");
        }

        printf("\n");
    }

    void printHelpMessage()
    {
        const char *text = "Commands can only contain one or two words. Two words are split by \":\" (without the quotes). White spaces of any kind are forbidden.\n"
                           "One-word commands apply to the PC, two-word commands apply to other creatures\n"
                           "To move the PC, type (without the quotes) \"[direction]\" where [direction] is north, south, east or west. If the PC cannot move into the selected direction (e.g. because there is no room in this direction or because the room there is full), a corresponding error message will be given and the PC will not move.\n"
                           "To move a creature, type (without the quotes) \"[Creature number]:[direction]\". If the creature cannot go there (for the same reasons as the PC), a corresponding message will be given, and the creature will react correspondingly (e.g., and NPC will grumble if it is asked to move to a full room, etc. See project description.)\n"
                           "To make the PC clean or dirty the current room, type \"clean\" or \"dirty\".\n"
                           "To make the PC look around the current room, type \"look\"\n"
                           "To make a creature clean or dirty the current room, type \"[Creature number]:clean\" or \"[Creature number]:dirty\".\n"
                           "To make a creature that is in the current room look around the room, type \"[Creature number]:look\". Error, if creature is not in the room.\n"
                           "To see this menu, type \"help\"\n"
                           "To exit the game, type \"exit\"";

        printf("%s", text);
    }

    ~Game()
    {
        debugPrint("Cleaning up Game\n");
        delete[] rooms;
        for (int i=0; numberOfCreatures; i++) {
            delete creatures[i];
        }
        delete creatures;
    }
};

#endif