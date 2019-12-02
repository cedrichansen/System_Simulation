public class Dealership extends Model<Car, Integer> {

    static final int TIME_TO_SELL_CAR = 10;
    double timeRemainingOnCurrentSale;

    int numberOfCarsToSell;

    public Dealership(Port<Car> in) {
        numberOfCarsToSell = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        lastKnownTime = new Time(0,0);
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
