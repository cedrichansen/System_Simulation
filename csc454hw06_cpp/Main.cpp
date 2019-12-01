#include "Port.cpp"
#include "Pipe.cpp"
#include "Machine.cpp"
#include "Network.cpp"
#include "Trajectory.cpp"


int  main () {

        /****** FOR HW05 ***/
        Port<int> * pressInPort = new Port<int>(0);

        Port<int> * netIn = {pressInPort};

        Port<int> * pressOutPort = new Port<int>(0);
        Port <int> * drillInPort = new Port<int>(0);

        Pipe<int> * p2 = new Pipe<int>(pressOutPort, drillInPort);

        Port<int> * drillOutPort = new Port<int>(0);

        Machine * press = new Machine(pressInPort, pressOutPort, 1, "press");

        Machine * drill = new Machine(pressInPort, pressOutPort, 2, "drill");

        Network<int, int> * network = new Network<int, int>(netIn, drillOutPort, 1);

        network->addModel(drill, "drill");
        network->addModel(press, "press");
        network->addPipe(p2, "p2");

        vector<Trajectory * > trajectories;

        /**
        2.0,2
        3.0,2
        15.0,2
         */
        string * ins1 = new string[1];
        ins1[0] = "2";
        Trajectory * i1 = new Trajectory(2.0, ins1, 1);
        
        string * ins2 = new string[1];
        ins2[0] = "2";
        Trajectory * i2 = new Trajectory(3.0,ins2, 1);

        string * ins3 = new string[1];
        ins3[0] = "2";
        Trajectory *  i3 = new Trajectory(15.0, ins3, 1);

        trajectories.push_back(i1);
        trajectories.push_back(i2);
        trajectories.push_back(i3);

        //(new Framework(network, getInputTrajectory("trajectory.txt"))).start();


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

