
public class Main{
    public static void main (String [] args) {
        
        Port netInPort = new Port();
        Port [] netIn = {netInPort};
        Port netOut = new Port();
        Network network = new Network(netIn, netOut);

        Port pressInPort = new Port();
        Pipe p1 = new Pipe(netInPort, pressInPort);
        
        Port pressOutPort = new Port();
        Port drillInPort = new Port();

        Pipe p2 = new Pipe(pressOutPort, drillInPort);

        Port drillOutPort = new Port();

        Pipe netOutPipe = new Pipe(drillOutPort, netOut);

        Press press = new Press(pressInPort, pressOutPort);

        Drill drill = new Drill(drillInPort, drillOutPort);

        network.add(drill, "drill");
        network.add(press, "press");

    }



}