public class VendingMachine extends Model {

    // a few helper variables
    final static int QUARTER_VALUE = 25;
    final static int DIME_VALUE = 10;
    final static int NICKEL_VALUE = 5;
    final static int COFFEE_PRICE = 100;

    int quarters;
    int dimes;
    int nickels;

    /** In cents, the value the customer has put into the machine */
    int customerValue;

    public VendingMachine(int initialN, int initialD, int initialQ) {
        nickels = initialN;
        dimes = initialD;
        quarters = initialQ;
    }

    @Override
    public String lambda() {

        String ret = "{";
        int coffees = numCoffeesToDispense();
        for (int i =0; i<coffees; i++) {
            ret += "COFFEE, ";
        }
        int change = amountOfChangeToGive(coffees);
        try {
            int[] coinsToGive = getChange(change);
            ret += getChangeCoinsStr(coinsToGive);

        } catch (VendingMachineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //remove last comma
        ret = ret.substring(0, ret.length() - 1);
        return ret + "}";
    }

    public String getChangeCoinsStr(int [] coins) {
        String coinsStr = "";
        for (int i=0; i<coins[0]; i++) {
            coinsStr += "n, ";
        }
        for (int i=0; i<coins[1]; i++) {
            coinsStr += "d, ";
        }
        for (int i=0; i<coins[2]; i++) {
            coinsStr += "q, ";
        }
        return coinsStr;
    }

    /**
     * Someone put a coin into the machine! yayyyyy, lets add some coins in
     * 
     * @throws IllegalInputException
     */
    @Override
    public void externalTransition(String input) throws IllegalInputException {
        if (input.equals("q")) {
            quarters++;
            customerValue += 25;
        } else if (input.equals("d")) {
            dimes++;
            customerValue += 10;
        } else if (input.equals("n")) {
            nickels++;
            customerValue += 5;
        } else {
            throw new IllegalInputException(input);
        }
    }

    /**
     * The timer experied on the vending machine..... Either give out a coffee, or
     * change, or both
     */
    @Override
    public void internalTranstion() {
        try {
            lambda();

            { /** Change state of the vending machine internally */
                int coffees = numCoffeesToDispense();
                customerValue -= coffees * COFFEE_PRICE;
                int change = amountOfChangeToGive(coffees);
                int[] coinsToGive = getChange(change);
                nickels -= coinsToGive[0];
                dimes -= coinsToGive[1];
                quarters -= coinsToGive[2];
            }
            
        } catch (VendingMachineException e) {
            e.printStackTrace();
        }
    }

    public int numCoffeesToDispense() {
        return (int) ((customerValue) / COFFEE_PRICE);
    }

    public int amountOfChangeToGive(int coffeesDispensed) {
        return (customerValue - (COFFEE_PRICE * coffeesDispensed));
    }

    public int[] getChange(int changeToDispense) throws VendingMachineException {

        float amountLeftToDispense = changeToDispense;
        int numberOfQuartersToDispense = 0;
        int numberOfDimesToDispense = 0;
        int numberOfNickelsToDispense = 0;

        if (amountLeftToDispense >= QUARTER_VALUE) {
            numberOfQuartersToDispense = (int) (amountLeftToDispense / QUARTER_VALUE);
            if (numberOfQuartersToDispense > quarters) { // we do not have enough quarters left to give
                                                         // quarters, so give out as many as possible
                numberOfQuartersToDispense = quarters;
            }
            amountLeftToDispense -= (QUARTER_VALUE * numberOfQuartersToDispense);
        }

        if (amountLeftToDispense >= DIME_VALUE) {
            numberOfDimesToDispense = (int) (amountLeftToDispense / DIME_VALUE);
            if (numberOfDimesToDispense > dimes) {
                numberOfDimesToDispense = dimes;
            }
            amountLeftToDispense -= (DIME_VALUE * numberOfDimesToDispense);
        }

        if (amountLeftToDispense >= NICKEL_VALUE) {
            numberOfNickelsToDispense = (int) (amountLeftToDispense / NICKEL_VALUE);
            if (numberOfNickelsToDispense > nickels) {
                numberOfNickelsToDispense = nickels;
            }
            amountLeftToDispense -= (NICKEL_VALUE * numberOfNickelsToDispense);
        }

        if (amountLeftToDispense != 0) {
            // we do not have enough coins to dispense correct amount of change
            throw new VendingMachineException(VendingMachineException.managementMessage);
        }

        int[] change = { numberOfNickelsToDispense, numberOfDimesToDispense, numberOfQuartersToDispense };
        return change;
    }

    /**
     * Someone put coins in EXACTLY when the time went off... Execute external,
     * followed by internal transitions
     * 
     * @throws IllegalInputException
     */
    @Override
    public void confluentTransition(String input) throws IllegalInputException {
        internalTranstion();
        externalTransition(input);
    }

    /**
     * If the customer value is 0, then time advance is infinity (double.maxValue),
     * else it is 2 seconds
     */
    @Override
    public double timeAdvance() {
        return customerValue > 0 ? (double) 2 : Double.MAX_VALUE;
    }

}