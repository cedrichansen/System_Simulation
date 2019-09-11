public class VendingMachine {

    static int QUARTER_VALUE = 25;
    static int DIME_VALUE = 10;
    static int NICKEL_VALUE = 5;
    static int COFFEE_PRICE = 100;


    int numberOfNickels;
    int numberOfDimes;
    int numberOfQuarters;


    //upon creation of the vending machine, the customer has not put in any coins;
    float customerValue = 0;

    boolean changeButtonPressed = false;


    public VendingMachine(int initialNickels, int initialDimes, int initialQuarters) {

        this.numberOfNickels = initialNickels;
        this.numberOfDimes = initialDimes;
        this.numberOfQuarters = initialQuarters;

    }


    public void dispenseCoffe() {
        customerValue -= COFFEE_PRICE;
        System.out.println("Dispensed coffee! Balance is now: " + customerValue);
    }

    public void getChange() {
        float amountToDispense = customerValue;
        float amountDispensed = 0;
        int numberOfQuartersToDispense = 0;
        int numberOfDimesToDispense = 0;
        int numberOfNickelsToDispense = 0;


        if (amountToDispense >= QUARTER_VALUE) {
            numberOfQuartersToDispense = (int) (amountToDispense / QUARTER_VALUE);
            if (numberOfQuartersToDispense > numberOfQuarters) { //we do not have enough quarters left to give quarters, so give out as many as possible
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


        numberOfNickels -= numberOfNickelsToDispense;
        numberOfDimes -= numberOfDimesToDispense;
        numberOfQuarters -= numberOfQuartersToDispense;

        assert (amountDispensed == customerValue);
        assert (amountToDispense == 0);

        customerValue -= amountDispensed;

        assert (customerValue == 0);

        System.out.println("Change has been returned: " + numberOfQuartersToDispense + " Quarters, " +
                numberOfDimesToDispense + " Dimes, " + numberOfNickelsToDispense + " Nickels. Total value returned: " +
                calculateValue(numberOfNickelsToDispense, numberOfDimesToDispense, numberOfQuartersToDispense)/100);
    }

    private float calculateValue(int nickels, int dimes, int quarters) {

        return (NICKEL_VALUE * nickels) + (DIME_VALUE * dimes) + (QUARTER_VALUE * quarters);

    }

    public void startSimulation() {
        //allow people to put in money and press buttons and stuff... Basically the loop that processes the sim

        String command = "";
        Main.s.nextLine(); // flush buffer again;

        do {

            System.out.println("Enter command: ");

            command = Main.s.nextLine();
            processAction(command);

        } while (!command.equals("exit"));

    }

    private void processAction(String command) {
        // possible inputs: nickel, dime, quarter, cancel, wait

        int quartersInserted = (int)command.chars().filter(ch -> ch == 'q').count();
        int dimesInserted = (int)command.chars().filter(ch -> ch == 'd').count();
        int nickelsInserted = (int)command.chars().filter(ch -> ch == 'n').count();
        int coinsInserted = (quartersInserted + dimesInserted + nickelsInserted);

        if (command.equals("wait")) {
            System.out.println("waiting for next tik... ");
            System.out.println(this.toString());

        } else if (command.equals("cancel")){
            //TODO ask if cancel means that the change button was pressed?
            if (customerValue > 0) {
                getChange();
            } else {
                System.out.println("No money has been inserted! No change to be given");
            }
            printBalance();


        } else if (command.length() == coinsInserted) {
            //if all things we typed were valid coins, then process them.
            addCoins(nickelsInserted, dimesInserted, quartersInserted);
            System.out.println("Added: " + nickelsInserted + " nickels, " + dimesInserted + " dimes, " + quartersInserted + " quarters\n" +
                    "Total added: " + calculateValue(nickelsInserted, dimesInserted, quartersInserted)/100);
            printBalance();

        } else {
            System.out.println("Unknown input.... please type a valid input");
        }

    }


    void addCoins(int nickels, int dimes, int quarters){
        numberOfQuarters +=quarters;
        numberOfDimes += dimes;
        numberOfNickels += nickels;

        customerValue += calculateValue(nickels, dimes, quarters);

    }

    public String toString() {
        return "Nickels: " + numberOfNickels + " Dimes: " + numberOfDimes + " Quarters: " + numberOfQuarters + "\nBalance: " + customerValue/100;
    }

    public void printBalance(){
        System.out.println("Balance: " + customerValue/100);
    }


}
