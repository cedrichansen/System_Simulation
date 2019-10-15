
/**
 * In the memory model, the state consists of a pair (x1, x2). The output of the model is x1, and the state transitions
 * on input x3, will result in (x2,x3).
 */
public class MemoryModel extends Model {

    public MemoryModel(int [] initial, Port in, Port out) {
        super(initial);
        this.outPort = out;
        this.numberOfInputs = 1;
        this.inPorts = new Port [numberOfInputs];
        this.inPorts[0] = in;
    }

    @Override
    public int lambda() {
        return this.state[0];
    }

    @Override
    public void delta(int [] inputSet) {
        int x1 = this.state[1];
        int x2 = inputSet[0];
        int [] stateVals = {x1, x2};
        this.state = stateVals;
    }

}