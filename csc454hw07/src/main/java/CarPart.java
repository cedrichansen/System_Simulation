public abstract class CarPart {

    String type;

    public CarPart (String t) {
        this.type = t;
    }

    @Override
    public abstract String toString();
}
