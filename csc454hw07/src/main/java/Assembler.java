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
        this.out.currentValue = new Car();
        return "Just assembled a new car!";
    }

    @Override
    public void externalTransition(Time currentTime, String in) {

        if (numBatteriesAvailable >= Car.numBatteriesRequired && numTiresAvailable >= Car.numWheelsRequired && numEnginesAvailable >= Car.numEnginesRequired) {
            //we received a piece, but we are already working on one.. decrement time appropriately
            Time elapsed = new Time(currentTime.realTime - lastKnownTime.realTime, 0);
            timeRemainingOnCurrentCar -= elapsed.realTime;
        } else {
            //we are starting our first piece, set time to processing time...
            timeRemainingOnCurrentCar = TIME_TO_ASSEMBLE_CAR;
        }

        // Figure out what we got
        if (this.in[0].currentValue != null) {
            this.numBatteriesAvailable++;
            this.in[0].currentValue = null;
        }
        if (this.in[1].currentValue != null) {
            this.numEnginesAvailable++;
            this.in[1].currentValue = null;
        }
        if (this.in[2].currentValue != null) {
            this.numTiresAvailable++;
            this.in[2].currentValue = null;
        }

    }

    @Override
    public void internalTransition() {

        this.numEnginesAvailable -= Car.numEnginesRequired;
        this.numTiresAvailable -= Car.numWheelsRequired;
        this.numBatteriesAvailable -= Car.numBatteriesRequired;

        this.lastKnownTime = new Time(this.lastKnownTime.realTime + timeRemainingOnCurrentCar, 0); // we might care to know the time at which processing time was reset
        this.timeRemainingOnCurrentCar = TIME_TO_ASSEMBLE_CAR;
    }

    @Override
    public void confluentTransition(Time currentTime, String in) {
        internalTransition();
        lastKnownTime = currentTime; //this is to prevent the press from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    @Override
    public Time timeAdvance() {
        if (numBatteriesAvailable >= Car.numBatteriesRequired && numTiresAvailable >= Car.numWheelsRequired && numEnginesAvailable >= Car.numEnginesRequired){
            return new Time(timeRemainingOnCurrentCar, 0);
        } else {
            return new Time(Double.MAX_VALUE,0);
        }
    }

    @Override
    public double getMaxTimeAdvance() {
        return Double.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "Assembler - Engines: " + numEnginesAvailable + " Batteries: " + numBatteriesAvailable + " Tires: " + numTiresAvailable + " time left: " + this.timeRemainingOnCurrentCar;
    }
}
