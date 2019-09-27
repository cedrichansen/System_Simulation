import Network.*;
import XOR.*;

import java.util.Scanner;

import Base.Input;
import Memory.*;

public class Main {

    static XORModel xor1;
    static XORModel xor2;
    static MemoryModel mm;
    static Network network;

    public static void main(String[] args) {

        initializeFields();

        Scanner sc = new Scanner(System.in);
        System.out.println("Type \"0/1 0/1\"");
        String command = sc.nextLine();

        while (!command.equals("exit") && !command.equals("quit")) {

            int[] input = convertToIntArray(command.split(" "));
            if (input.length == 2) {
                Input netIn = new NetworkInput(input);
                System.out.println(propagateTick(netIn));
            } else {
                System.out.println("Input must be only 2 values, 0 or 1, with a space in between");
            }
            
            System.out.println("Type \"0/1 0/1\"");
            command = sc.nextLine();
        }

    }

    private static int propagateTick(Input netIn) {
        int xor1Out = xor1.tick(netIn);
        int xor2Out = xor2.lambda(xor2.state);
        int [] memInInts = {xor2Out};
        Input memIn = new MemoryInput(memInInts);
        int memOut = mm.tick(memIn);
        int [] xor2inInts = {xor1Out, memOut};
        Input xor2In = new XORInput(xor2inInts);
        xor2Out = xor2.tick(xor2In);

        return xor2Out;
    }

    private static void initializeFields() {
        int[] in = { 0 };
        NetworkState initialState = new NetworkState(in);
        network = new Network(initialState);

        /** Initialise the atomic models */
        XORState xorInitialState = new XORState(in);
        xor1 = new XORModel(xorInitialState);
        xor2 = new XORModel(xorInitialState);

        int[] mmInitial = { 0, 0 };
        MemoryState mmInitialState = new MemoryState(mmInitial);
        mm = new MemoryModel(mmInitialState);
    }

    private static int[] convertToIntArray(String[] strs) {
        int[] ints = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            ints[i] = Integer.parseInt(strs[i]);
        }
        return ints;
    }

}
