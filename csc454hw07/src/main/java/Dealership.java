public class Dealership extends Model<Car, Car> {

    static final int TIME_TO_SELL_CAR = 25;
    double timeRemainingOnCurrentSale;

    int numberOfCarsToSell;

    public Dealership(Port<Car> in, Port<Car> out) {
        numberOfCarsToSell = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        this.out = out;
        lastKnownTime = new Time(0,0);
    }


    @Override
    public String lambda() {
        this.out.currentValue = new Car();
        numberOfCarsToSell--;
        return "Dealership just sold a car!";
    }

    @Override
    public void externalTransition(Time currentTime, String in) {
        if (numberOfCarsToSell > 0 ) {
            Time elapsed = new Time(currentTime.realTime - lastKnownTime.realTime, 0);
            timeRemainingOnCurrentSale -= elapsed.realTime;
        } else {
            timeRemainingOnCurrentSale = TIME_TO_SELL_CAR;
        }

        Car c = this.in[0].currentValue;
        if (c != null) {
            this.numberOfCarsToSell++;
            this.in[0].currentValue = null;
        }

        lastKnownTime = currentTime;
    }

    @Override
    public void internalTransition() {
        this.lastKnownTime = new Time(this.lastKnownTime.realTime + timeRemainingOnCurrentSale, 0); // we might care to know the time at which processing time was reset
        this.timeRemainingOnCurrentSale = TIME_TO_SELL_CAR;
    }

    @Override
    public void confluentTransition(Time currentTime, String in) {
        internalTransition();
        lastKnownTime = currentTime; //this is to prevent the model from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    @Override
    public Time timeAdvance() {
        if (numberOfCarsToSell == 0) {
            return new Time(Double.MAX_VALUE, 0);
        } else {
            return new Time(timeRemainingOnCurrentSale, 0);
        }
    }

    @Override
    public double getMaxTimeAdvance() {
        return Double.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "Car dealership - cars to sell: " + this.numberOfCarsToSell + " time left on sale: " + timeRemainingOnCurrentSale ;

    }
}
