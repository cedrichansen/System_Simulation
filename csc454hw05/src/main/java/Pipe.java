public class Pipe {

    Port sending;
    Port receiving;

    public Pipe(Port outgoingPort, Port receivingPort) {
        this.sending = outgoingPort;
        this.receiving = receivingPort;
    }

    public void passValue(){
        receiving.currentValue += sending.currentValue;
    }

}