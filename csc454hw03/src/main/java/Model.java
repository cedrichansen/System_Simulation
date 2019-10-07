

public abstract class Model {
    public static boolean verbose;
	public int [] state;

    public Model(int [] initial) {
        this.state = initial;
    }

    public int tickNumber = 0;

    public abstract int tick(int [] in);
    public abstract int lambda();
    public abstract int [] delta(int [] inputSet);
    
    public void debugPrint(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

}
