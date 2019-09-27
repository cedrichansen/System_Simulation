import Network.*;
import XOR.*;
import Base.Input;
import Memory.*;

public class Main {


    public static void main(String [] args) {

        int [] in = {0};
        NetworkState initialState = new NetworkState(in);
        Network network = new Network(initialState);

        /** Initialise the atomic models */
        XORState xorInitialState = new XORState(in);
        XORModel xor1 = new XORModel(xorInitialState);
        XORModel xor2 = new XORModel(xorInitialState);

        int [] mmInitial = {0,0};
        MemoryState mmInitialState = new MemoryState(mmInitial);
        MemoryModel mm = new MemoryModel(mmInitialState);


        network.add(xor1);
        network.add(xor2);
        network.add(mm);

        int [] t = {1,0};
        int [] a = {0,0};
        Input i = new XORInput(t);
        Input j = new XORInput(a);
        System.out.println(xor1.tick(i));
        System.out.println(xor1.tick(i));
        System.out.println(xor1.tick(j));

    }

}
