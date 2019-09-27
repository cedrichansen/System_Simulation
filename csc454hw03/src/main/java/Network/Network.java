package Network;

import java.util.ArrayList;

import Base.*;

public class Network extends Model {

    public Network(NetworkState initial) {
        super(initial);
        // TODO Auto-generated constructor stub
    }

    ArrayList<Model> childModels = new ArrayList<Model>();

    public void add(Model m) {
        childModels.add(m);
    }



    //TODO: some sort of function called like connect or something, that gets output of one model, into aniother and so on


    @Override
    public int tick(NetworkInput input) {
        // Chain inputs and outputs togetther here, and produce output

    }

    @Override
    public int lambda(State currentState) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public State delta(State currentState, Input inputSet) {
        // TODO Auto-generated method stub
        return null;
    }

    

}