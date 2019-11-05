public abstract class Model {
   
    Network parent;
    Port<Integer> [] in;
    Port<Integer> out;
    int numberOfInputs;
    Time prevKnownTime;
    int numberOfPartsToProcess;
    Time lastKnownTime;
    boolean con;

    public void addParent (Network n) {
        parent = n;
    }

    public abstract String lambda();

    public abstract void externalTransition(Time elapsedTime, String in);

    public abstract void internalTransition();

    public abstract void confluentTransition(Time elapsedTime, String in);

    public abstract Time timeAdvance();

    public abstract double getMaxTimeAdvance();

    public abstract String toString();

}