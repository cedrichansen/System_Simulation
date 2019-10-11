

public class XORModel extends Model {

    public XORModel(int [] initial, Port input1, Port input2, Port output) {
        super(initial);
        this.outPort = output;
        this.numberOfInputs = 2;
        this.inPorts = new Port [numberOfInputs];
        this.inPorts[0] = input1;
        this.inPorts[1] = input2;
    }

    public int lambda() {
        return this.state[0]; //corresponds to the statebit here
    }

    public int [] delta(int [] inputSet) {
        int res = inputSet[0] ^ inputSet[1];
        int [] stateVal = {res};
        return stateVal;
    }

}