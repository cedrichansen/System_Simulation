import java.util.ArrayList; 

public abstract class Model {

    public static boolean verbose;
    public int [] state;

    public int numberOfInputs;

    public Port outPort;
    public Port [] inPorts;

    public Model(int [] initial) {
        this.state = initial;
    }

    public int tickNumber = 0;

    public abstract int lambda();
    public abstract int [] delta(int [] inputSet);
    
    public void debugPrint(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    public int [] convertInPortsToIntArr() {
        int [] vals = new int [numberOfInputs];
        for (int i = 0; i<numberOfInputs; i++) {
            vals[i] = inPorts[i].currentValue;
        }
        return vals;
    }

}
