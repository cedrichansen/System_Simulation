package XOR;

import Base.*;

public class XORModel extends Model {

    public XORModel(XORState initial) {
        super(initial);
    }

    public int tick(Input in) {
        int out = lambda(this.state);
        this.state = delta(this.state, in);
        return out;
    }

    public int lambda(State currentState) {
        return currentState.stateVariables[0]; //corresponds to the statebit here
    }

    public State delta(State currentState, Input inputSet) {
        int res = inputSet.inputValues[0] ^ inputSet.inputValues[1];
        int [] stateVal = {res};
        XORState s = new XORState(stateVal);
        return s;
    }

}