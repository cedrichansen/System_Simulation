public class EngineManufacturer extends Model<Order, Engine> {

    int TIME_TO_PROCESS_PIECE;
    double timeRemainingOnPiece;

    public EngineManufacturer(Port<Order> in, Port<Engine> out) {
        numberOfPartsToProcess = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        this.out = out;
        lastKnownTime = new Time(0, 0);
        TIME_TO_PROCESS_PIECE = 4;
    }


    @Override
    public String lambda() {
        numberOfPartsToProcess--;
        this.out.currentValue = new Engine("engine");
        return "Engine was created";

    }

    @Override
    public void externalTransition(Time currentTime, String in) {
        if (numberOfPartsToProcess > 0) {
            //we received a piece, but we are already working on one.. decrement time appropriately
            Time elapsed = new Time(currentTime.realTime - lastKnownTime.realTime, 0);
            timeRemainingOnPiece -= elapsed.realTime;
        } else {
            //we are starting our first piece, set time to processing time...
            timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        }

        // Figure out how many pieces we need to make
        Order order = this.in[0].currentValue;
        this.numberOfPartsToProcess += order.numberOfEngines;
        lastKnownTime = currentTime;
    }

    @Override
    public void internalTransition() {
        this.lastKnownTime = new Time(this.lastKnownTime.realTime + timeRemainingOnPiece, 0); // we might care to know the time at which processing time was reset
        this.timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
    }

    @Override
    public void confluentTransition(Time currentTime, String in) {
        internalTransition();
        lastKnownTime = currentTime; //this is to prevent the press from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    @Override
    public Time timeAdvance() {
        if (numberOfPartsToProcess == 0) {
            return new Time(Double.MAX_VALUE, 0);
        } else {
            return new Time(timeRemainingOnPiece, 0);
        }
    }

    @Override
    public double getMaxTimeAdvance() {
        return Double.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "Engine manufacturer - Pieces: " + this.numberOfPartsToProcess + " Time left: " + this.timeRemainingOnPiece;
    }
}
