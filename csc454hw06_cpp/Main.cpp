#include "Port.cpp"
#include "Press.cpp"
#include "Pipe.cpp"
#include "Drill.cpp"
#include "Network.cpp"



int  main () {

        /****** FOR HW05 ***/


        Port<int> * pressInPort = new Port<int>(0);

        Port<int> * netIn = {pressInPort};

        Port<int> * pressOutPort = new Port<int>(0);
        Port <int> * drillInPort = new Port<int>(0);

        Pipe<int> * p2 = new Pipe<int>(pressOutPort, drillInPort);

        Port<int> * drillOutPort = new Port<int>(0);

        Press<int, int> * press = new Press<int, int>(pressInPort, pressOutPort);

        Drill<int, int> * drill = new Drill<int, int>(drillInPort, drillOutPort);

        Network<int, int> * network = new Network<int, int>(netIn, drillOutPort);

        network->addChild(drill, "drill");
        network->addChild(press, "press");
        network->addPipe(p2);

        (new Framework(network, getInputTrajectory("trajectory.txt"))).start();


        /****** FOR HW03 MODELS *****/

        //TODO: Initialize ports for XOR model to -1
        // Port<int> xor1in1 = new Port<>(NULL);
        // Port<int> xor1in2 = new Port<> (NULL);
        // Port * xor1ins = {xor1in1, xor1in2};
        // Port<int> xor1out = new Port<>(NULL);
        // XORModel xor1 = new XORModel(xor1ins, xor1out);



        // Port<int> xor2in1 = new Port<>(null);
        // Port<int> xor2in2 = new Port<> (null);
        // Port * xor2ins = {xor2in1, xor2in2};
        // Port <int> xor2out = new Port<>(null);
        // XORModel xor2 = new XORModel(xor2ins, xor2out);


        // Port<int> mmIn = new Port<>(null);
        // Port<int> mmOut = new Port<>(null);
        // Port * mmInArr = {mmIn};
        // MemoryModel mm = new MemoryModel(mmInArr, mmOut);

        // Pipe <int> x1x2 = new Pipe<>(xor1out, xor2in1);
        // Pipe <int> mmx2 = new Pipe<>(mmOut, xor2in2);
        // Pipe <int> x2mm = new Pipe<>(xor2out, mmIn);

        // Network <int, int> XORNetwork = new Network<>(xor1ins, xor2out);
        // XORNetwork.addChild(xor1, "xor1");
        // XORNetwork.addChild(xor2, "xor2");
        // XORNetwork.addChild(mm, "mm");
        // XORNetwork.addPipe(x1x2);
        // XORNetwork.addPipe(mmx2);
        // XORNetwork.addPipe(x2mm);

        // vector<Trajectory> traj = getInputTrajectory("xorTrajectory.txt");
        // (new Framework(XORNetwork, traj, 150, 3, traj.size())).start();

        return 0;
    }

