import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) throws FileNotFoundException, InterruptedException {

        /****** FOR HW05 ***/

        Port<Integer> pressInPort = new Port<>(0);

        Port [] netIn = {pressInPort};

        Port<Integer> pressOutPort = new Port<>(0);
        Port <Integer>drillInPort = new Port<>(0);

        Pipe<Integer> p2 = new Pipe<>(pressOutPort, drillInPort);

        Port<Integer> drillOutPort = new Port<>(0);

        Press press = new Press(pressInPort, pressOutPort);

        Drill drill = new Drill(drillInPort, drillOutPort);

        Network <Integer, Integer> network = new Network<>(netIn, drillOutPort);

        network.addChild(drill, "drill");
        network.addChild(press, "press");
        network.addPipe(p2);

        Framework f = new Framework(network, getInputTrajectory("trajectory.txt"));
        f.start();



        /****** FOR HW03 MODELS *****/

        //TODO: Initialize ports for XOR model to -1
        Port<Integer> xor1in1 = new Port<Integer>(-1);
        Port<Integer> xor1in2 = new Port<Integer> (-1);
        Port[] xor1ins = {xor1in1, xor1in2};
        Port<Integer> xor1out = new Port<Integer>(-1);
        XORModel xor1 = new XORModel(xor1ins, xor1out);



        Port<Integer> xor2in1 = new Port<Integer>(-1);
        Port<Integer> xor2in2 = new Port<Integer> (-1);
        Port[] xor2ins = {xor2in1, xor2in2};
        Port <Integer> xor2out = new Port<Integer>(-1);
        XORModel xor2 = new XORModel(xor2ins, xor2out);


        Port<Integer> mmIn = new Port<Integer>(-1);
        Port<Integer> mmOut = new Port<Integer>(-1);
        Port [] mmInArr = {mmIn};
        MemoryModel mm = new MemoryModel(mmInArr, mmOut);


    }


    static ArrayList<Trajectory> getInputTrajectory(String fileName) throws FileNotFoundException{
        Scanner fs = new Scanner(new File(fileName));
        
        ArrayList<Trajectory> inputs = new ArrayList<>();

        String line = "";

        while (fs.hasNextLine()) {
            line = fs.nextLine();
            String [] fields = line.split(",");
            String [] in = new String[fields.length-1];
            //fields[0] is the time, and 1,2,3 ... are the inputs
            System.arraycopy(fields, 1, in,0, in.length);
            inputs.add(new Trajectory(Double.parseDouble(fields[0]), in));
        }
        
        fs.close();
        return inputs;
    }



}