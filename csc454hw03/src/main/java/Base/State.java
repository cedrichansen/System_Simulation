package Base;

public abstract class State {
    
    public int [] stateVariables;

    public State(int [] initialState) {
        this.stateVariables = initialState;
    }    

}