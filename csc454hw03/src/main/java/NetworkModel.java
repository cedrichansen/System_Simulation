

public class NetworkModel extends Model {

    public NetworkModel(int [] initial) {
        super(initial);
    }


    public int tick(int [] input) {
        // Chain inputs and outputs togetther here, and produce output
        return tickNumber++;
    }

    public int lambda() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int [] delta(int [] inputSet) {
        // TODO Auto-generated method stub
        return null;
    }

    

}