public class MemoryModel extends Model <Integer, Integer> {

    final static int TIME_TO_PROCESS_PIECE = 1;
    double timeRemaining;

    int [] state;

    public MemoryModel(Port<Integer>[] in, Port<Integer> out){
        this.state = new int [2];
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        for (int i =0; i<numberOfInputs; i++) {
            this.in[i] = in[i];
        }
        this.out = out;
        lastKnownTime = new Time(0,0);
    }

    @Override
    public String lambda() {
        this.out.currentValue = state[0];
        return this.out.currentValue.toString();
    }

    @Override
    public void externalTransition(Time elapsedTime, String in) {
        Integer inp = this.in[0].currentValue;
        this.in[0].currentValue = -1;
        inp = inp < 0 ? Integer.parseInt(in) : inp; //if network input, input will come from in param rather than pipe

        int x1 = this.state[1];
        int x2 = inp;
        int [] stateVals = {x1, x2};
        this.state = stateVals;
    }

    @Override
    public void internalTransition() {
        //not used
    }

    @Override
    public void confluentTransition(Time currentTime, String in) {
        internalTransition();
        lastKnownTime = currentTime; //this is to prevent the press from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    @Override
    public Time timeAdvance() {
        return new Time(1, 0);
    }

    @Override
    public double getMaxTimeAdvance() {
        return Double.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "Memory Model - state : " + state[0] + ", " + state[1];
    }
}
