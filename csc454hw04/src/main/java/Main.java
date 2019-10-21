import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main 
{
    public static void main( String[] args) throws FileNotFoundException, InterruptedException {
        VendingMachine vm = new VendingMachine(0,0,0);

        ArrayList<String[]> inputs = getInputTrajectory("trajectory.txt");

        (new Framework(inputs, vm)).start();

    }



    /** Assume input trajectory is formatted correctly */
    /** In the case of the vending machine, format is
     *  time,input
     * where time, in seconds, is format #.#, and input is a single element from set {q,d,n}
     * ex: 
     * 1.2,q
     * 2.1,n
     * 
     */
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
