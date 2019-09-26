public class VendingMachine {

    // a few helper variables
    static int QUARTER_VALUE = 25;
    static int DIME_VALUE = 10;
    static int NICKEL_VALUE = 5;
    static int COFFEE_PRICE = 100;
    private int tikNumber = 0;

    // 5 variables representing machine state
    private int numberOfNickels;
    private int numberOfDimes;
    private int numberOfQuarters;
    private float customerValue = 0;
    private boolean changeButtonPressed = false;

    public VendingMachine(int initialNickels, int initialDimes, int initialQuarters) {

        this.numberOfNickels = initialNickels;
        this.numberOfDimes = initialDimes;
        this.numberOfQuarters = initialQuarters;
    }

    public void dispenseCoffee(boolean output) {
        int numberOfCoffees = (int) ((customerValue) / COFFEE_PRICE);

        if (numberOfCoffees > 0) {
            if (!output) {
                customerValue -= numberOfCoffees * COFFEE_PRICE;
            } else {
                System.out.println("Dispensed " + numberOfCoffees + " coffee(s)! Balance is now: " + ((customerValue - (numberOfCoffees*COFFEE_PRICE))/ 100));
            }
        }

    }

    /**
     * Dispenses change to the user.
     * 
     * @throws VendingMachineException
     */
    public void getChange(boolean output) throws VendingMachineException {

        if (changeButtonPressed) {
            float amountToDispense = customerValue;
            float amountDispensed = 0;
            int numberOfQuartersToDispense = 0;
            int numberOfDimesToDispense = 0;
            int numberOfNickelsToDispense = 0;

            if (amountToDispense >= QUARTER_VALUE) {
                numberOfQuartersToDispense = (int) (amountToDispense / QUARTER_VALUE);
                if (numberOfQuartersToDispense > numberOfQuarters) { // we do not have enough quarters left to give
                                                                     // quarters, so give out as many as possible
                    numberOfQuartersToDispense = numberOfQuarters;
                }
                amountToDispense -= (QUARTER_VALUE * numberOfQuartersToDispense);
                amountDispensed += (QUARTER_VALUE * numberOfQuartersToDispense);
            }

            if (amountToDispense >= DIME_VALUE) {
                numberOfDimesToDispense = (int) (amountToDispense / DIME_VALUE);
                if (numberOfDimesToDispense > numberOfDimes) {
                    numberOfDimesToDispense = numberOfDimes;
                }
                amountToDispense -= (DIME_VALUE * numberOfDimesToDispense);
                amountDispensed += (DIME_VALUE * numberOfDimesToDispense);
            }

            if (amountToDispense >= NICKEL_VALUE) {
                numberOfNickelsToDispense = (int) (amountToDispense / NICKEL_VALUE);
                if (numberOfNickelsToDispense > numberOfNickels) {
                    numberOfNickelsToDispense = numberOfNickels;
                }
                amountToDispense -= (NICKEL_VALUE * numberOfNickelsToDispense);
                amountDispensed += (NICKEL_VALUE * numberOfNickelsToDispense);
            }

            if (amountDispensed != customerValue) {
                //we do not have enough coins to dispense correct amount of change
                throw new VendingMachineException(VendingMachineException.managementMessage);
            }

            if (!output) {
                // The output function cannot modify the state, so we hold on to the state so that the state transition function can make the state change
                numberOfNickels -= numberOfNickelsToDispense;
                numberOfDimes -= numberOfDimesToDispense;
                numberOfQuarters -= numberOfQuartersToDispense;

                customerValue -= amountDispensed;
                changeButtonPressed = false;

            } else {
                // The output function will be printing out values, aka, producing output
                if (amountDispensed != 0) {
                    System.out.println("Change has been returned: " + numberOfQuartersToDispense + " Quarters, "
                    + numberOfDimesToDispense + " Dimes, " + numberOfNickelsToDispense
                    + " Nickels. Total value returned: "
                    + calculateValue(numberOfNickelsToDispense, numberOfDimesToDispense, numberOfQuartersToDispense)
                            / 100);
                } else {
                    System.out.println("Change button was pressed, but there is no balance to dispense");
                }
                
            }

        }

    }

    /**
     * 
     * @param nickels
     * @param dimes
     * @param quarters
     * @return The corresponding dollar value given the input params
     */
    private float calculateValue(int nickels, int dimes, int quarters) {

        return (NICKEL_VALUE * nickels) + (DIME_VALUE * dimes) + (QUARTER_VALUE * quarters);

    }

    public void startSimulation() throws VendingMachineException {
        // allow people to put in money and press buttons and stuff... Basically the
        // loop that processes the sim

        String command = "";
        Main.s.nextLine(); // flush buffer again;

        do {

            System.out.println("Enter command: ");

            command = Main.s.nextLine();
            processAction(command);

        } while (!command.equals("exit"));

    }

    /**
     * Processes a user command. This is the top level function of processing
     * vending machine actions
     * 
     * @param command the input passed in by the user
     */
    private void processAction(String command) throws VendingMachineException {

        if (command.equals("exit")) {
            return;
        }

        tikNumber++;

        // process output from the current state
        dispenseCoffee(true);
        getChange(true);

        adjustState();

        // modify the state based on the input
        char[] commands = command.toCharArray();
        int quartersInserted = 0;
        int dimesInserted = 0;
        int nickelsInserted = 0;

        for (char c : commands) {
            if (c == 'w') {
                // do nothing
            } else if (c == 'q') {
                // quarter inserted
                quartersInserted++;
            } else if (c == 'd') {
                // dime inserted
                dimesInserted++;
            } else if (c == 'n') {
                // nickel inserted
                nickelsInserted++;
            } else if (c == 'c') {
                System.out.println("Change button was pressed! Change will be dispensed on next tik");
                changeButtonPressed = true;
            } else {
                System.out.println(c + " is an invalid token, and will be ignored");
            }
        }

        if (quartersInserted != 0 || nickelsInserted != 0 || dimesInserted != 0) {
            // at least one type of change was inserted
            addCoins(nickelsInserted, dimesInserted, quartersInserted);
            System.out.println("Added: " + nickelsInserted + " nickels, " + dimesInserted + " dimes, "
                    + quartersInserted + " quarters\n" + "Total added: "
                    + calculateValue(nickelsInserted, dimesInserted, quartersInserted) / 100);
        }

        System.out.println(this.toString());

    }

    /**
     * Purpose of this function is to look at the state of the machine, and adjust
     * it for whatever was outputted. eg, if we have $1.50 in the machine, and the
     * change button was not pressed, then dispense a coffee... That sort of thing
     */
    private void adjustState() throws VendingMachineException {
        
        dispenseCoffee(false);
        getChange(false);

    }

    /**
     * adds this many coins to the vending machine
     * 
     * @param nickels  number of nickels to be added
     * @param dimes    number of dimes to be added
     * @param quarters number of quarters to be added
     */
    void addCoins(int nickels, int dimes, int quarters) {
        numberOfQuarters += quarters;
        numberOfDimes += dimes;
        numberOfNickels += nickels;

        customerValue += calculateValue(nickels, dimes, quarters);
    }

    public String toString() {
        return "---Vending Machine info---" + "\nNickels: " + numberOfNickels + " Dimes: " + numberOfDimes
                + " Quarters: " + numberOfQuarters + "\nBalance: " + customerValue / 100 + "\nCurrent tik " + tikNumber
                + "\n";
    }

}
