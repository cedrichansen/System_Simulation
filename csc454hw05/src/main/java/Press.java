public class Press extends Model{

    final static int TIME_TO_PROCESS_PIECE = 1;
    double timeRemainingOnPiece;

    public Press(Port<Integer> in, Port<Integer> out){
        numberOfPartsToProcess = 0;
        this.in = new Port[1];
        this.in[0] = in;
        this.numberOfInputs = 1;
        prevKnownTime = new Time(0,0);
        this.out = out;
        prevKnownTime = new Time(0,0);
    }

    public String lambda() {
        if (this.out.currentValue != null) {
            this.out.currentValue += 1;
        } else {
            this.out.currentValue = 1;
        }

        return "Press finished one part!";
    }

    public void externalTransition(Time elapsedTime, String in) {
        Integer partsAdded = this.in[0].currentValue;
        this.in[0].currentValue = 0;
        if (numberOfPartsToProcess > 0) {
            numberOfPartsToProcess += partsAdded;
            //timeRemainingOnPiece -= elapsedTime.realTime;
        } else {
            if (partsAdded == null) {
                numberOfPartsToProcess = Integer.parseInt(in);
                timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
            } else {
                if (partsAdded != 0) {
                    numberOfPartsToProcess += partsAdded;
                } else {
                    numberOfPartsToProcess = Integer.parseInt(in);
                }

                timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
            }
        }
    }

    public void internalTransition() {
        this.numberOfPartsToProcess--;
        this.timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        parent.passPipeValues();
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