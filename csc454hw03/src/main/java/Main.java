
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.File;

public class Main {

    static NetworkModel network;

    public static void main(String[] args) {

        parseArgs(args);

        initializeNetworks();

        getUserInput();
    }

    /**
     * This function propagates the input throughout the network
     */
    private static int propagateTick(int[] netIn) {


        network.tick(netIn);

        return network.outPort.currentValue;
    }

    private static void initializeNetworks() {

        int[] in = { 0 };

        Port xor1_In1 = new Port();
        Port xor1_In2 = new Port();
        Port xor1_out = new Port();
        XORModel xor1 = new XORModel(in, xor1_In1, xor1_In2, xor1_out);

        Port xor2_In1 = new Port();
        Port xor2_In2 = new Port();
        Port xor2_out = new Port();
        XORModel xor2 = new XORModel(in, xor2_In1, xor2_In2, xor2_out);

        int[] mmInitial = { 0, 0 };
        Port mmIn = new Port();
        Port mmOut = new Port();
        MemoryModel mm = new MemoryModel(mmInitial, mmIn, mmOut);

        network = new NetworkModel(in, xor1, xor2, 3);

        network.addPipe(new Pipe(xor1_out, xor2_In1));
        network.addPipe(new Pipe(mmOut, xor2_In2));
        network.addPipe(new Pipe(xor2_out, mmIn));

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

    private static void processBatch(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            String[] strs = line.split(" ");
            if (strs.length == 2) {
                try {
                    int[] input = convertToIntArray(line.split(" "));
                    System.out.println(input[0] + " " + input[1]);
                    if (Model.verbose) {
                        System.out.println("Network output: " + propagateTick(input));
                    } else {
                        System.out.println(propagateTick(input));
                    }
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Input must be only 2 values, 0 or 1, with a space in between");
            }

            System.out.println();

        }

        sc.close();

    }

    static void parseArgs(String[] args) {
        if (args.length != 0) {
            if (args[0].equals("-v")) {
                System.out.println("Verbose mode has been enabled");
                Model.verbose = true;
            } else {
                System.out.println("Unknown command line option. Only valid option is \"-v\"");
            }
        }

    }

    public static void getUserInput() {
        System.out.println(
                "\nOn each tick, enter  \"0/1 0/1\" for an interactive input, \nor \"-b <filename>\" to process batch input from a file\n");

        Scanner sc = new Scanner(System.in);
        System.out.println("Type \"0/1 0/1\" or \"-b <filename>\" when prompted for input");
        String command = sc.nextLine();

        while (!command.equals("exit") && !command.equals("quit")) {

            // process batch mode
            if (command.split(" ")[0].equals("-b")) {
                if (command.split(" ").length != 2) {
                    System.out.println("Must specify a filename after -b");
                } else {
                    try {
                        processBatch(command.split(" ")[1]);
                    } catch (FileNotFoundException e) {
                        System.out.println("File \"" + command.split(" ")[1] + "\" cannot be found");
                    }
                }

            } else {
                // process regular interactive input
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
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.println();
            command = sc.nextLine();

        }

        sc.close();

    }

}
