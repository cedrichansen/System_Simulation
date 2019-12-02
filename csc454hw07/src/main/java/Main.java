import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) throws FileNotFoundException, InterruptedException {

        Port<String> opIn = new Port<String>(null);
        Port<Order> opOut = new Port<Order>(null);
        OrderProcessor op = new OrderProcessor(opIn, opOut);

        Port<Order> tireIn = new Port<Order>(null);
        Port<Tire> tireOut = new Port<Tire>(null);
        Manufacturer<Tire> tireMaker = new Manufacturer<Tire>(tireIn, tireOut, 1, "Tire manufacturer");

        Port<Order> engineIn = new Port<Order>(null);
        Port<Engine> engineOut = new Port<Engine>(null);
        Manufacturer<Engine> engineMaker = new Manufacturer<Engine>(engineIn, engineOut, 4, "Engine manufacturer");

        Port<Order> batteryIn = new Port<Order>(null);
        Port<Battery> batteryOut = new Port<Battery>(null);
        Manufacturer<Battery> batteryMaker = new Manufacturer<>(batteryIn, batteryOut, 2, "Battery manufacturer");

        Port<Battery> assemblerBatt = new Port<Battery>(null);
        Port<Engine> assemglerEng = new Port<Engine>(null);
        Port<Tire> assemblerTire = new Port<Tire>(null);
        Port [] assemblerIns = {assemblerBatt, assemglerEng, assemblerTire};
        Port <Car> assemblerOut = new Port<Car>(null);
        Assembler carAssembler = new Assembler(assemblerIns, assemblerOut);

        Port<Car> dealerIn = new Port<Car>(null);
        Port<Car> dealerOut = new Port<Car> (null);
        Dealership dealership = new Dealership(dealerIn, dealerOut);

        Pipe<Order> orderToTire = new Pipe<Order>(opOut, tireIn);
        Pipe<Order> orderToEngine = new Pipe<Order>(opOut, engineIn);
        Pipe<Order> orderToBattery = new Pipe<Order>(opOut, batteryIn);

        Pipe<Battery> battToAssembler = new Pipe<Battery>(batteryOut, assemblerBatt);
        Pipe<Engine> engineToAssembler = new Pipe<Engine>(engineOut, assemglerEng);
        Pipe<Tire> tireToAssembler = new Pipe<Tire>(tireOut, assemblerTire);

        Pipe<Car> assemblerToDealer  = new Pipe<Car>(assemblerOut, dealerIn);

        Network<String, Car> carPipeline = new Network<String, Car>(opIn, dealerOut);

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