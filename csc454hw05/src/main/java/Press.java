public class Press extends Model{

    final static int TIME_TO_PROCESS_PIECE = 1;
    int numberOfPartsToProcess;
    double timeRemainingOnPiece;
    Port <Integer> out;
    Port <Integer> [] in;

    public Press(Port<Integer> in, Port<Integer> out){
        numberOfPartsToProcess = 0;
        this.in = new Port[1];
        this.in[0] = in;
        this.numberOfInputs = 1;
        prevKnownTime = new Time(0,0);
        this.out = out;
    }

    public String lambda() {
        out.currentValue += 1;
        return "Press finished one part!";
    }

    public void externalTransition(Time elapsedTime, String in) {
        Integer partsAdded = this.in[0].currentValue;
        this.in[0].currentValue = 0;
        if (numberOfPartsToProcess > 0) {
            numberOfPartsToProcess += partsAdded;
            timeRemainingOnPiece -= (elapsedTime.realTime - prevKnownTime.realTime);
        } else {
            if (partsAdded == null) {
                numberOfPartsToProcess = 0;
            } else {
                numberOfPartsToProcess += partsAdded;
                timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
            }
        }
    }

    public void internalTransition() {
        this.numberOfPartsToProcess--;
        this.timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        this.out.currentValue += 1;
        parent.passPipeValues(); //tell the parent that something happend
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
        return "Press- Number of part: " + numberOfPartsToProcess + " Time remaining on current part: " + timeRemainingOnPiece;
    }

}