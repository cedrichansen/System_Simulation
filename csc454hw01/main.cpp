#include <cstdio> //for printf
#include <stdio.h> //for scanf


#include "Game.cpp"


int main(int argc, char** argv) 
{ 
    //passing in any args will launch debug mode
    Game game;
    if (argc != 1) {
        game.initialize(true);
    } else {
        game.initialize(false);
    }

    game.play();


    return 0; 
} 