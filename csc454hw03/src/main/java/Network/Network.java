package Network;

import java.util.ArrayList;

import Base.*;

public class Network extends Model {

    public Network(NetworkState initial) {
        super(initial);
    }


    public int tick(Input input) {
        // Chain inputs and outputs togetther here, and produce output
        return tickNumber++;
    }

    public int lambda(State currentState) {
        // TODO Auto-generated method stub
        return 0;
    }

    public State delta(State currentState, Input inputSet) {
        // TODO Auto-generated method stub
        return null;
    }

    

}