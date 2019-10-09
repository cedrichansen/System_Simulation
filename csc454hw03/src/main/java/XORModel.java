

public class XORModel extends Model {

    public XORModel(int [] initial) {
        super(initial);
    }

    public void tick(int [] in) {
        int out = lambda();
        this.state = delta(in);
    }

    public int lambda() {
        //debugPrint(this.state[0] + "");
        return this.state[0]; //corresponds to the statebit here
    }

    public int [] delta(int [] inputSet) {
        int res = inputSet[0] ^ inputSet[1];
        int [] stateVal = {res};
        return stateVal;
    }

}