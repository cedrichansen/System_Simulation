import java.util.ArrayList;

public class Framework {

    ArrayList<String[]> inputTrajectory;

    /**
     * Framework currently only supports one model
     */
    Model model;
    int discreteTime;

    public Framework(ArrayList<String[]> inputTrajectory, Model m) {
        this.inputTrajectory = inputTrajectory;
        this.model = m;
        discreteTime = 0;
    }

    public void start() throws InterruptedException {
        System.out.println(
                "Welcome to vending machine simulator xTreme 9000\nOutput/Input will automatically be processed as per the specified input trajectory\n");
        float timeSincePreviousEvent = 0;
        float sleepTimer = 0;
        float timeElapsed = 0;
        float inputTime = 0;
        float timeAdvance = 0;

        for (int i = 0; i < inputTrajectory.size(); i++) {

            timeAdvance = (float) model.timeAdvance();
            inputTime = Float.parseFloat(inputTrajectory.get(i)[0]);
            timeSincePreviousEvent = inputTime - timeElapsed;
            sleepTimer = timeAdvance > timeSincePreviousEvent ? timeSincePreviousEvent : timeAdvance;

            Thread.sleep((long) sleepTimer * 1000);
            timeElapsed += sleepTimer;


            processStep(inputTrajectory.get(i), timeSincePreviousEvent, timeAdvance, timeElapsed);

            //since the internal transition happened, we still need to process the current input
            if (sleepTimer != timeSincePreviousEvent) {
                timeAdvance = (float) model.timeAdvance();
                timeSincePreviousEvent = inputTime - timeElapsed;
                sleepTimer = timeAdvance > timeSincePreviousEvent ? timeSincePreviousEvent : timeAdvance;

                Thread.sleep((long) sleepTimer * 1000);
                timeElapsed += sleepTimer;

                processStep(inputTrajectory.get(i), timeSincePreviousEvent, timeAdvance, timeElapsed);
            }
        }

        //let the internal clock expire if needed
        if (model.timeAdvance() != model.getMaxTimeAdvance()) {
            Thread.sleep((long) (1000 * model.timeAdvance()));
            System.out.println("Real Time: " + (timeElapsed + model.timeAdvance()) + " Output: " + model.internalTransition());
            System.out.println(model.toString() + "\n");
        }

    }

    public void processStep(String[] input, float timeSinceLastInput, float timeAdvance, float realTime) {

        try {
            if (timeSinceLastInput > timeAdvance) {
                // execute internal transition
                System.out.println("Real Time: " + realTime + " Output: " + model.internalTransition());


            } else if (timeSinceLastInput < timeAdvance) {
                // execute external transition
                System.out.println("Real Time: " + realTime + " Input: " + input[1]);
                model.externalTransition(input[1]);

            } else {
                // They must be equal! confluent case
                System.out.println("Real Time: " + realTime + " Input:" + input[1]);
                System.out.println("Output: " + model.confluentTransition(input[1]));
            }
            System.out.println(model.toString() + "\n");
        } catch (IllegalInputException e) {
            e.printStackTrace();
        }

    }


}