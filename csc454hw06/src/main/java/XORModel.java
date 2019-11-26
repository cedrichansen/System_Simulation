public class XORModel extends Model<Integer, Integer> {

    final static int TIME_TO_PROCESS_PIECE = 1;
    double timeRemaining;

    int [] state;

    public XORModel(Port<Integer>[] in, Port<Integer> out){
        this.state = new int [1];
        this.numberOfInputs = 2;
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
        return "XOR returned: " + this.out.currentValue;
    }

    @Override
    public void externalTransition(Time elapsedTime, String in) {

        Integer inp1 = this.in[0].currentValue;
        this.in[0].currentValue = -1;
        inp1 = inp1 < 0 ? Integer.parseInt(in.split(",")[0]) : inp1; //if network input, input will come from in param rather than pipe

        Integer inp2 = this.in[0].currentValue;
        this.in[0].currentValue = -1;
        inp2 = inp2 < 0 ? Integer.parseInt(in.split(",")[1]) : inp2; //if network input, input will come from in param rather than pipe

        this.state[0] = inp1 ^ inp2;
    }

    @Override
    public void internalTransition() {
        //Not used
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
        return "XOR Model - state : " + state[0] ;
    }
}
