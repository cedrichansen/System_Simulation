

import java.util.Scanner;
import java.util.regex.Pattern;

import javax.sound.sampled.SourceDataLine;

public class Main {

    static XORModel xor1;
    static XORModel xor2;
    static MemoryModel mm;
    static NetworkModel network;
    public static void main(String[] args) {
        if (args.length != 0) {
            if (args[0].equals("-v")) {
                System.out.println("Verbose mode has been enabled");
                Model.verbose = true;
            }
        }

        initializeFields();

        Scanner sc = new Scanner(System.in);
        System.out.println("Type \"0/1 0/1\"");
        String command = sc.nextLine();

        while (!command.equals("exit") && !command.equals("quit")) {

            try {
                int[] input = convertToIntArray(command.split(" "));
                if (input.length == 2) {
                    if (Model.verbose) {
                        System.out.println("Network output: " + propagateTick(input));
                    } else {
                        System.out.println(propagateTick(input));
                    }
                } else {
                    System.out.println("Input must be only 2 values, 0 or 1, with a space in between");
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            } catch (InvalidInputException e){
                System.out.println(e.getMessage()); 
            }
            
            System.out.println("\nType \"0/1 0/1\"");
            command = sc.nextLine();
        }

        sc.close();

    }

    /**
     * This function propagates the input throughout the network
     */
    private static int propagateTick(int [] netIn) {

       network.tick(netIn);
       return network.output;
    }

    private static void initializeFields() {
        
        int[] in = { 0 };
        network = new NetworkModel(in);

        xor1 = new XORModel(in);
        xor2 = new XORModel(in);

        int[] mmInitial = { 0, 0 };
        mm = new MemoryModel(mmInitial);

        network.addModel("xor1", xor1);
        network.addModel("mm", mm);
        network.addModel("xor2", xor2);
    }

    private static int[] convertToIntArray(String[] strs) throws NumberFormatException, InvalidInputException {
        int[] ints = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            if (Pattern.matches("[a-zA-Z]+", strs[i])) {
                throw new NumberFormatException("Cannot enter non numeric values");
            } else {
                if (!strs[i].equals("1") && !strs[i].equals("0")) {
                    throw new InvalidInputException("Can only enter 0, or 1");
                }
                ints[i] = Integer.parseInt(strs[i]);
            }
        }
        return ints;
    }

}

/**
 * 
 * MemoryModel testing
 * 
        // int [] zero = {0};
        // int [] one = {1};
        // MemoryInput oneIn = new MemoryInput(one);
        // MemoryInput zeroIn = new MemoryInput(zero);

        // System.out.println(mm.tick(oneIn));
        // System.out.println(mm.tick(oneIn));
        // System.out.println(mm.tick(zeroIn));
        // System.out.println(mm.tick(oneIn));
        // System.out.println(mm.tick(zeroIn));
 */