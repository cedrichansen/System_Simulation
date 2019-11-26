public class Press extends Model<Integer, Integer> {

    final static int TIME_TO_PROCESS_PIECE = 1;
    double timeRemainingOnPiece;

    public Press(Port<Integer> in, Port<Integer> out){
        numberOfPartsToProcess = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        this.out = out;
        lastKnownTime = new Time(0,0);
    }

    public String lambda() {
        this.out.currentValue = 1;
        return "Press finished one part!";
    }

    public void externalTransition(Time currentTime, String in) {

        if (numberOfPartsToProcess > 0) {
            //we received a piece, but we are already working on one.. decrement time appropriately
            Time elapsed = new Time(currentTime.realTime - lastKnownTime.realTime, 0);
            timeRemainingOnPiece -= elapsed.realTime;
        } else {
            //we are starting our first piece, set time to processing time...
            timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        }

        lastKnownTime = currentTime;

        Integer partsAdded = this.in[0].currentValue;
        this.in[0].currentValue = 0;

        partsAdded = partsAdded == 0 ? Integer.parseInt(in) : partsAdded; //if network input, input will come from in param rather than pipe
        numberOfPartsToProcess += partsAdded;

    }

    public void internalTransition() {
        this.numberOfPartsToProcess--;
        this.timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
    }

    public void confluentTransition(Time currentTime, String in) {
        internalTransition();
        lastKnownTime = currentTime; //this is to prevent the press from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    public Time timeAdvance() {
        if (numberOfPartsToProcess == 0) {
            return new Time(Double.MAX_VALUE, 0);
        } else {
            return new Time(timeRemainingOnPiece, 0);
        }
    }

    public double getMaxTimeAdvance() {
        return Double.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "Press- Number of parts: " + numberOfPartsToProcess + " Time remaining on current part: " + timeRemainingOnPiece;
    }


}