public abstract class Model {
   
    Network parent;
    Port [] in;
    Port out;
    int numberOfInputs;

    public void addParent (Network n) {
        parent = n;
    }

    public abstract String lambda();

    public abstract void externalTransition(Time elapsedTime);

    public abstract void internalTransition();

    public abstract void confluentTransition(Time elapsedTime);

    public abstract Time timeAdvance();

    public abstract double getMaxTimeAdvance();




}