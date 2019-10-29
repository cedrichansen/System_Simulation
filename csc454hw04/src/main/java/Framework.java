import java.util.ArrayList;

public class Framework {

    ArrayList<String[]> inputTrajectory;

    /**
     * Framework currently only supports one model
     */
    Model model;
    int discreteTime;
    final double THRESHOLD = 0.01;
    final Time MIN_INCREMENT = new Time(0, 1);

    public Framework(ArrayList<String[]> inputTrajectory, Model m) {
        this.inputTrajectory = inputTrajectory;
        this.model = m;
        discreteTime = 0;
    }

    public void start() throws InterruptedException {
        System.out.println(
                "Welcome to vending machine simulator xTreme 9000\nOutput/Input will automatically be processed as per the specified input trajectory\n");
        Time timeSincePreviousEvent;
        Time sleepTimer;
        Time timeElapsed = new Time(0,0);
        Time inputTime;
        Time timeAdvance;

        for (int i = 0; i < inputTrajectory.size(); i++) {

            timeAdvance = model.timeAdvance(); 
            inputTime = new Time(Double.parseDouble(inputTrajectory.get(i)[0]), 0); //real input events are always 0 on discrete axis
            timeSincePreviousEvent = inputTime.since(timeElapsed);

            if (timeAdvance.realTime > timeSincePreviousEvent.realTime) {
                sleepTimer = timeSincePreviousEvent;
            } else {
                sleepTimer = timeAdvance;
            }

            Thread.sleep((long) sleepTimer.realTime * 1000);

            timeElapsed = timeElapsed.timeAdvance(sleepTimer);


            processStep(inputTrajectory.get(i), timeSincePreviousEvent, timeAdvance, timeElapsed);

            //since the internal transition happened, we still need to process the current input
            if (sleepTimer.realTime != timeSincePreviousEvent.realTime) {
                timeAdvance = model.timeAdvance();
                timeSincePreviousEvent = inputTime.since(timeElapsed);
                if (timeAdvance.realTime > timeSincePreviousEvent.realTime) {
                    sleepTimer = timeSincePreviousEvent;
                } else {
                    sleepTimer = timeAdvance;
                }

                Thread.sleep((long) sleepTimer.realTime * 1000);
                timeElapsed = timeElapsed.timeAdvance(sleepTimer);

                processStep(inputTrajectory.get(i), timeSincePreviousEvent, timeAdvance, timeElapsed);
            }
        }

        //let the internal clock expire if needed
        if (model.timeAdvance().realTime != model.getMaxTimeAdvance()) {
            Thread.sleep((long) (1000 * model.timeAdvance().realTime));
            timeElapsed = timeElapsed.timeAdvance(model.timeAdvance());
            System.out.println("internal");
            System.out.println("Real Time: " + timeElapsed.realTime + " Output: " + model.lambda());
            model.internalTransition();
            System.out.println(model.toString() + "\n");
        }

    }

    public void processStep(String[] input, Time timeSinceLastInput, Time timeAdvance, Time realTime) {

        try {
                if (timeSinceLastInput.greaterThan(timeAdvance)) {
                // execute internal transition
                System.out.println("internal");
                System.out.println("Real Time: " + realTime.realTime); 
                System.out.println("Output: " + model.lambda());
                model.internalTransition();

            } else if (timeAdvance.greaterThan(timeSinceLastInput)) {
                // execute external transition
                System.out.println("external");
                System.out.println("Real Time: " + realTime.realTime + "\nInput: " + input[1]);
                model.externalTransition(input[1]);

            } else {
                // They must be equal! confluent case
                System.out.println("confluent");
                System.out.println("Real Time: " + realTime.realTime + " Input:" + input[1]);
                System.out.println("Output: " + model.lambda());
                model.confluentTransition(input[1]);
            }
            System.out.println(model.toString() + "\n");

            timeAdvance = model.timeAdvance();

            while (timeAdvance.realTime == 0) {
                // Whatever model needs to do if timeadvance is 0 --- does not happen in this current project
                timeAdvance = timeAdvance.timeAdvance(MIN_INCREMENT); //advance by minimum increment instead
                System.out.println("Time: " + timeAdvance.toString());
                System.out.println("Output: " + model.lambda());
                model.internalTransition();
                timeAdvance = model.timeAdvance();
            }
        } catch (IllegalInputException e) {
            System.out.println(e.message);
        }

    }


}