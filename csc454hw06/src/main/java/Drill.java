public class Drill extends Model<Integer, Integer>{

    final static int TIME_TO_PROCESS_PIECE = 2;
    double timeRemainingOnPiece;

    public Drill(Port<Integer> in, Port<Integer> out){
        numberOfPartsToProcess = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        this.out = out;
        lastKnownTime = new Time(0,0);
    }

    public String lambda() {
        this.out.currentValue = (int)this.out.currentValue + 1;
        return "Drill finished one part!";
    }

    public void externalTransition(Time currentTime, String in) {
        Integer partsAdded = this.in[0].currentValue;
        this.in[0].currentValue = 0;

        partsAdded = partsAdded == 0 ? Integer.parseInt(in) : partsAdded; //if network input, input will come from in param rather than pipe
        numberOfPartsToProcess += partsAdded;

        if (numberOfPartsToProcess > 0) {
            Time elapsed = new Time(currentTime.realTime - lastKnownTime.realTime, 0);
            timeRemainingOnPiece -= elapsed.realTime;
        } else {
            //we are starting our first piece, set time to processing time...
            timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        }

        lastKnownTime = currentTime;
    }

    public void internalTransition() {
        this.numberOfPartsToProcess--;
        this.timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
    }

    public void confluentTransition(Time elapsedTime, String in) {
        internalTransition();
        externalTransition(elapsedTime, in);
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