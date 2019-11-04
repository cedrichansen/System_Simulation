public class Pipe {

    Port<Integer> sending;
    Port<Integer> receiving;

    public Pipe(Port<Integer> outgoingPort, Port<Integer>receivingPort) {
        this.sending = outgoingPort;
        this.receiving = receivingPort;
    }

    /**
     * Move the value from the sendingPort to the receiving port. 
     * Reset the sending value so we do not send the same value more than onece
     */
    public void passValue(){
        if (sending.currentValue != null) {
            if (receiving.currentValue != null) {
                receiving.currentValue += sending.currentValue;
            } else {
                receiving.currentValue = sending.currentValue;
            }
        }
        sending.currentValue = 0;
    }

}