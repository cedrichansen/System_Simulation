public abstract class Model {
   
    Network parent;
    Port [] in;
    Port out;
    int numberOfInputs;
    Time prevKnownTime;

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