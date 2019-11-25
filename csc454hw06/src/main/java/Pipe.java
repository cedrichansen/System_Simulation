public class Pipe <T> {

    Port<T> sending;
    Port<T> receiving;

    public Pipe(Port<T> outgoingPort, Port<T>receivingPort) {
        this.sending = outgoingPort;
        this.receiving = receivingPort;
    }

    /**
     * Move the value from the sendingPort to the receiving port.
     */
    public void passValue(){
        receiving.currentValue = sending.currentValue;
    }

}