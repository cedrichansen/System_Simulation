package Base;

public abstract class Model {
    public static boolean verbose;
	public State state;

    public Model(State initial) {
        this.state = initial;
    }

    public int tickNumber = 0;

    public abstract int tick(Input in);
    public abstract int lambda(State currentState);
    public abstract State delta(State currentState, Input inputSet);
    
    public void debugPrint(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

}
