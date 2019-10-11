import java.util.HashMap;
import java.util.ArrayList;

public class NetworkModel extends Model {

    HashMap<String, Model> children;
    ArrayList<Pipe> pipes;

    public NetworkModel(int[] initial, Port in1, Port in2, Port out) {
        super(initial);
        children = new HashMap<String, Model>();
        pipes = new ArrayList<Pipe>();
        this.numberOfInputs = 2;
        this.inPorts = new Port [numberOfInputs];
        this.inPorts[0] = in1;
        this.inPorts[1] = in2;
        this.outPort = out;
    }

    public void tick(int[] netIn) {

        /** Lambda's */
        lambda();

        for (int i =0; i<netIn.length; i++) {
            this.inPorts[i].currentValue = netIn[i];
        }

        /** Pass all pipe values around */
        executePipes();
        
        /** Delta's */
        delta(netIn);
    }

    public int lambda() {
        for (String model : children.keySet()) {
            Model currentModel = children.get(model);
            currentModel.outPort.currentValue = currentModel.lambda();
            debugPrint("Lambda from "+ model + ": " + currentModel.lambda());
        }
        return 0;
    }

    public int[] delta(int[] inputSet) {
        for (String model : children.keySet()) {
            Model currentModel = children.get(model);
            int [] inputs = currentModel.convertInPortsToIntArr();
            currentModel.state = currentModel.delta(inputs);
            if (verbose) {
                String state = "";
                for (int i = 0; i < currentModel.state.length; i++) {
                    state += currentModel.state[i] + " ";
                }
                state = state.substring(0, state.length() - 1);
                System.out.println("New state for " + model + ": {" + state + "}");
            }
        }

        return null;
    }

    public void executePipes(){
        for (Pipe p: pipes) {
            p.passValue();
        }
    }

    public void addModel(String modelName, Model m) {
        this.children.put(modelName, m);
    }

    public void addPipe(Pipe p) {
        pipes.add(p);
    }

}