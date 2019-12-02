public class Manufacturer<OUT extends CarPart> extends Model<Order, OUT> {

    // Here, OUT is whatever the particular model needs to produce, eg: Tire, Engine or Battery

    int TIME_TO_PROCESS_PIECE;
    double timeRemainingOnPiece;
    String name;


    public Manufacturer(Port<Order> in, Port<OUT> out, int timeToMakePiece, String name) {
        numberOfPartsToProcess = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        this.out = out;
        lastKnownTime = new Time(0,0);
        TIME_TO_PROCESS_PIECE = timeToMakePiece;
        this.name = name;
    }


    @Override
    public String lambda() {
        return null;
    }

    @Override
    public void externalTransition(Time elapsedTime, String in) {

    }

    @Override
    public void internalTransition() {

    }

    @Override
    public void confluentTransition(Time elapsedTime, String in) {

    }

    @Override
    public Time timeAdvance() {
        return null;
    }

    @Override
    public double getMaxTimeAdvance() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
