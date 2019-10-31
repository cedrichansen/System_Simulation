public class Drill extends Model {

    final static int TIME_TO_PROCESS_PIECE = 2;
    int numberOfPartsToProcess;
    double timeRemainingOnPiece;

    public Drill(Port in, Port out){
        numberOfPartsToProcess = 0;
        this.in = new Port[1];
        this.in[0] = in;
        this.numberOfInputs = 1;
    }

    public String lambda() {
        out.currentValue = 1;
        return "Drill finished one part!";
    }

    public void externalTransition(Time elapsedTime) {
        int partsAdded = this.in[0].currentValue;
        this.in[0].currentValue = 0;
        if (numberOfPartsToProcess > 0) {
            numberOfPartsToProcess += partsAdded;
            timeRemainingOnPiece -= elapsedTime.realTime;
        } else {
            numberOfPartsToProcess += partsAdded;
            timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
        }
    }

    public void internalTransition() {
        this.numberOfPartsToProcess--;
        this.timeRemainingOnPiece = TIME_TO_PROCESS_PIECE;
    }

    public void confluentTransition(Time elapsedTime) {
        internalTransition();
        externalTransition(elapsedTime);
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

}