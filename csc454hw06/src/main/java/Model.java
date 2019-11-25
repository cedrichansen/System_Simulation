public abstract class Model<IN,OUT> {
   
    Network parent;

    Port<IN> [] in;
    Port<OUT> out;

    int numberOfInputs;
    Time timeRemainingOnCurrentPiece;
    int numberOfPartsToProcess;

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