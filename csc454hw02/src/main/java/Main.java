/*

Implement and simulate an atomic model of a vending machine. The machine sells coffee and accepts only nickels, dimes,
and quarters. When $1 has been inserted, the machine dispenses a cup of coffee. When the CHANGE button is pressed, the
machine returns any unspent coins.

The model accepts any combinations of inputs from the set {nickel, dime, quarter, cancel, wait} and produces
combinations of output from the set {nickel, dime, quarter, coffee, nothing}. The state of the model consists of five
variables: the number of quarters, nickels, and dimes in the machine, respectively; the total value that the user has
entered so far; and whether the CHANGE button was pressed. The output function produces a coffee output for every
100 cents' value the user entered, and, if the state of the CHANGE button is true, it also produces a correct
combination of nickel, dime, and quarter outputs for (value % 100). The state transition function alters the state of
the machine appropriately (i.e., processes the input, and also decreases the value and the number of coins accordingly
to account for what the output function does). It's a good idea to apply the output function before the state
transition in order not to have to keep stale states around.

 */


import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    static Scanner s = new Scanner(System.in);

    public static void main(String [] args) {

        System.out.println("Welcome to the vending machine simulator! \n");

        int nickels =0, dimes =0, quarters = 0;

        boolean successfulInput = false;
        while (!successfulInput) {

            System.out.println("Please enter the number of nickels, dimes and quarters to start with in the vending machine.\n" +
                    "Format: \"n d q\" (Separated by spaces)");

            try {
                nickels = s.nextInt();
                dimes = s.nextInt();
                quarters = s.nextInt();
                successfulInput = true;
            } catch (NumberFormatException | InputMismatchException ime) {
                System.out.println("Incorrect values specified!");
                s.nextLine(); // clear buffer
            }

        }

        VendingMachine vm = new VendingMachine(nickels,dimes,quarters);
        vm.startSimulation();

    }
}
