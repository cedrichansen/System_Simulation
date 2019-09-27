package Memory;

import Base.Input;
import Base.Model;
import Base.State;

/**
 * In the memory model, the state consists of a pair (x1, x2). The output of the model is x1, and the state transitions
 * on input x3, will result in (x2,x3).
 */
public class MemoryModel extends Model {

    public MemoryModel(MemoryState initial) {
        super(initial);
    }

    public int tick(Input input) {
        int out = lambda(this.state);
        this.state = delta(this.state, input);
        return out;
    }

    public int lambda(State currentState) {
        return currentState.stateVariables[0];
    }

    public State delta(State currentState, Input inputSet) {
        int x1 = this.state.stateVariables[1];
        int x2 = inputSet.inputValues[0];
        int [] stateVals = {x1, x2};
        MemoryState ms = new MemoryState(stateVals);
        return ms;
    }

}