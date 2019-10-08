

public class NetworkModel extends Model {

    public NetworkModel(int [] initial) {
        super(initial);
    }


    public int tick(int [] netIn) {
        //index 0 is xor1 model
        //index 1 is mm model
        //index 2 is xor2 model

        /** Lambda's */  
        int xor1Out = children.get(0).lambda();
        int memOut = children.get(1).lambda();
        int xor2Out = children.get(2).lambda();
        

        /** Delta's */
        children.get(0).state = children.get(0).delta(netIn);
        int [] mmIn = {xor2Out};
        children.get(1).state = children.get(1).delta(mmIn);

        int [] xor2In = {memOut, xor1Out};
        children.get(2).state = children.get(2).delta(xor2In);

        return xor2Out;
    }


    public void addModel(Model m) {
        this.children.add(m);
    }

    public int lambda() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int [] delta(int [] inputSet) {
        // TODO Auto-generated method stub
        return null;
    }

    

}