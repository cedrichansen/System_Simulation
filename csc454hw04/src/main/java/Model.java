public abstract class Model{

    public abstract String lambda();
    public abstract void externalTransition(String input) throws IllegalInputException;
    public abstract void internalTranstion();
    public abstract void confluentTransition(String input) throws IllegalInputException;
    public abstract double timeAdvance();

}