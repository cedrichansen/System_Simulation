#!/bin/bash

rm *.o
g++ -c *.cpp
g++ *.o -o main
