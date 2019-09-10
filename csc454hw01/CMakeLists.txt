cmake_minimum_required(VERSION 3.8)
project(csc454hw01)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES
        Creature.cpp
        Game.cpp
        main.cpp
        Room.cpp)

add_executable(csc454hw01 ${SOURCE_FILES})