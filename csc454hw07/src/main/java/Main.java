import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) throws FileNotFoundException, InterruptedException {

        Port<String> opIn = new Port<>(null);
        Port<Order> opOut = new Port<>(null);
        OrderProcessor op = new OrderProcessor(opIn, opOut);

        Port<Order> tireIn = new Port<>(null);
        Port<Tire> tireOut = new Port<>(null);
        Manufacturer<Tire> tireMaker = new Manufacturer<>(tireIn, tireOut, 1, "Tire manufacturer");

        Port<Order> engineIn = new Port<>(null);
        Port<Engine> engineOut = new Port<>(null);
        Manufacturer<Engine> engineMaker = new Manufacturer<>(engineIn, engineOut, 4, "Engine manufacturer");

        Port<Order> batteryIn = new Port<>(null);
        Port<Battery> batteryOut = new Port<>(null);
        Manufacturer<Battery> batteryMaker = new Manufacturer<>(batteryIn, batteryOut, 2, "Battery manufacturer");

        Port<Battery> assemblerBatt = new Port<>(null);
        Port<Engine> assemlerEng = new Port<>(null);
        Port<Tire> assemblerTire = new Port<>(null);
        Port [] assemblerIns = {assemblerBatt, assemlerEng, assemblerTire};
        Port <Car> assemblerOut = new Port<>(null);
        Assembler carAssembler = new Assembler(assemblerIns, assemblerOut);

        Port<Car> dealerIn = new Port<>(null);
        Port<Car> dealerOut = new Port<> (null);
        Dealership dealership = new Dealership(dealerIn, dealerOut);

        Pipe<Order> orderToBattery = new Pipe<>(opOut, batteryIn);
        Pipe<Order> orderToTire = new Pipe<>(opOut, tireIn);
        Pipe<Order> orderToEngine = new Pipe<>(opOut, engineIn);

        Pipe<Battery> battToAssembler = new Pipe<>(batteryOut, assemblerBatt);
        Pipe<Engine> engineToAssembler = new Pipe<>(engineOut, assemlerEng);
        Pipe<Tire> tireToAssembler = new Pipe<>(tireOut, assemblerTire);

        Pipe<Car> assemblerToDealer  = new Pipe<>(assemblerOut, dealerIn);

        Network<String, Car> carPipeline = new Network<>(opIn, dealerOut);

        carPipeline.addChild(op, "orderProcessor");
        carPipeline.addChild(tireMaker, "tire maker");
        carPipeline.addChild(engineMaker, "engine maker");
        carPipeline.addChild(batteryMaker, "battery maker");
        carPipeline.addChild(carAssembler, "assembler");
        carPipeline.addChild(dealership, "dealership");

        carPipeline.addPipe(orderToTire);
        carPipeline.addPipe(orderToBattery);
        carPipeline.addPipe(orderToEngine);

        carPipeline.addPipe(battToAssembler);
        carPipeline.addPipe(engineToAssembler);
        carPipeline.addPipe(tireToAssembler);

        carPipeline.addPipe(assemblerToDealer);

        (new Framework(carPipeline, getInputTrajectory("carAssembly.txt"))).start();

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