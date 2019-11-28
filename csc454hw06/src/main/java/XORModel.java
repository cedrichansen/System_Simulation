public class XORModel extends Model<Integer, Integer> {

    final static int TIME_TO_PROCESS_PIECE = 1;
    double timeRemaining;

    int[] state;

    public XORModel(Port<Integer>[] in, Port<Integer> out) {
        this.state = new int[1];
        this.numberOfInputs = 2;
        this.in = new Port[numberOfInputs];
        for (int i = 0; i < numberOfInputs; i++) {
            this.in[i] = in[i];
        }
        this.out = out;
        lastKnownTime = new Time(0, 0);
    }


    @Override
    public String lambda() {
        this.out.currentValue = state[0];
        return this.out.currentValue.toString();
    }

    @Override
    public void externalTransition(Time elapsedTime, String in) {

        boolean inputsAreReady = true;
        boolean haveInput = !in.equals("");


        if (haveInput) {
            //actually put the input somewhere
            for (int i =0; i<numberOfInputs; i++) {
                if (this.in[i].currentValue == null) {
                    //give a port the value
                    this.in[i].currentValue = Integer.parseInt(in);
                    break;
                }
            }
        }


        for (int i = 0; i < numberOfInputs; i++) {
            if (this.in[i].currentValue == null) {
                //someone set the value
                inputsAreReady = false;
                break;
            }
        }

        if (inputsAreReady) {
            this.state[0] = this.in[0].currentValue ^ this.in[1].currentValue;

            //reset inputs
            for (int i = 0; i < numberOfInputs; i++) {
                this.in[i].currentValue = null;
            }

        } else {
            System.out.println("Inputs are not ready for xor model");
        }


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
        return "XOR Model - state : " + state[0];
    }
}
