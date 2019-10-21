public abstract class Model{

    public abstract String lambda();
    public abstract void externalTransition(String input) throws IllegalInputException;
    public abstract String internalTransition();
    public abstract String confluentTransition(String input) throws IllegalInputException;
    public abstract double timeAdvance();
    public abstract double getMaxTimeAdvance();
    public abstract String toString();

}