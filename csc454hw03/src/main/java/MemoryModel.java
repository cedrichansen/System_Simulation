
/**
 * In the memory model, the state consists of a pair (x1, x2). The output of the model is x1, and the state transitions
 * on input x3, will result in (x2,x3).
 */
public class MemoryModel extends Model {

    public MemoryModel(int [] initial) {
        super(initial);
    }

    public int tick(int [] input) {
        int out = lambda();
        this.state = delta(input);
        return out;
    }

    public int lambda() {
        return this.state[0];
    }

    public int [] delta(int [] inputSet) {
        int x1 = this.state[1];
        int x2 = inputSet[0];
        int [] stateVals = {x1, x2};
        return stateVals;
    }

}