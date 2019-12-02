public class Assembler extends Model<CarPart, Car> {


    static final int TIME_TO_ASSEMBLE_CAR = 10;
    double timeRemainingOnCurrentCar;

    int numEnginesAvailable;
    int numBatteriesAvailable;
    int numTiresAvailable;


    public Assembler(Port<CarPart> [] in, Port<Car> out) {
        numberOfPartsToProcess = 0;
        this.numberOfInputs = 3;
        this.in = new Port[numberOfInputs];
        this.in[0] = in[0];
        this.in[1] = in[1];
        this.in[2] = in[2];
        this.out = out;
        lastKnownTime = new Time(0,0);
        numBatteriesAvailable = 0;
        numTiresAvailable = 0;
        numEnginesAvailable = 0;
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
