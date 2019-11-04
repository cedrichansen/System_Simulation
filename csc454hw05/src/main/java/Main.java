import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) throws FileNotFoundException, InterruptedException {
        
        Port<Integer> netInPort = new Port<Integer>();
        Port [] netIn = {netInPort};
        Port<Integer> netOut = new Port<Integer>();
        Network network = new Network(netIn, netOut);

        Port<Integer> pressInPort = new Port<Integer>();
        Pipe p1 = new Pipe(netInPort, pressInPort);
        
        Port<Integer> pressOutPort = new Port<Integer>();
        Port <Integer>drillInPort = new Port<Integer>();

        Pipe p2 = new Pipe(pressOutPort, drillInPort);

        Port<Integer> drillOutPort = new Port<Integer>();

        Pipe netOutPipe = new Pipe(drillOutPort, netOut);

        Press press = new Press(pressInPort, pressOutPort);

        Drill drill = new Drill(drillInPort, drillOutPort);

        network.addChild(drill, "drill");
        network.addChild(press, "press");
        network.addPipe(p1);
        network.addPipe(p2);
        network.addPipe(netOutPipe);

        Framework f = new Framework(network, getInputTrajectory("trajectory.txt"));
        f.start();

    }


    static ArrayList<String[]> getInputTrajectory(String fileName) throws FileNotFoundException{
        Scanner fs = new Scanner(new File(fileName));
        
        ArrayList<String []> inputs = new ArrayList<>();

        String line = "";

        while (fs.hasNextLine()) {
            line = fs.nextLine();
            inputs.add(line.split(","));
        }
        
        fs.close();
        return inputs;
    }



}