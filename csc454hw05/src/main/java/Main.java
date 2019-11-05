import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) throws FileNotFoundException, InterruptedException {
        

        Port<Integer> pressInPort = new Port<Integer>();

        Port [] netIn = {pressInPort};

        Port<Integer> pressOutPort = new Port<Integer>();
        Port <Integer>drillInPort = new Port<Integer>();

        Pipe p2 = new Pipe(pressOutPort, drillInPort);

        Port<Integer> drillOutPort = new Port<Integer>();

        Press press = new Press(pressInPort, pressOutPort);

        Drill drill = new Drill(drillInPort, drillOutPort);

        Network network = new Network(netIn, drillOutPort);

        network.addChild(drill, "drill");
        network.addChild(press, "press");
        network.addPipe(p2);

        network.startingModel = press;

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