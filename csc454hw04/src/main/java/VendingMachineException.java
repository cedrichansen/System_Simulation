public class VendingMachineException extends Exception {

    String message;

    final static String managementMessage = "OH NO! Looks like the vending machine does not have the right combination of coins to produce the correct change :("
            + "\nPlease contact management (613-898-2930) to refill the coins in the vending machine, AND, to receive a complimentary coffee, on us, free of charge!"
            + "\nWe again apologize for this inconvenience, and we hope to be able to better service you next time!";

    VendingMachineException(String message) {
        super(message);
    }

}