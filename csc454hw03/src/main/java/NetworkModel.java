import java.util.HashMap;

public class NetworkModel extends Model {

    HashMap<String, Model> children;

    int output;

    public NetworkModel(int[] initial) {
        super(initial);
        children = new HashMap<String, Model>();
    }

    public void tick(int[] netIn) {

        /** Lambda's */
        lambda();

        networkInputLink(netIn, children.get("xor1"));
        dualInputLink(children.get("xor1"), children.get("mm"), children.get("xor2"));
        simpleLink(children.get("xor2"), children.get("mm"));
        networkOutputLink(children.get("xor2"));

        /** Delta's */
        delta(netIn);
    }

    public int lambda() {
        for (String model : children.keySet()) {
            Model currentModel = children.get(model);
            debugPrint("Lambda from "+ model + ": " + currentModel.lambda());
        }
        return 0;
    }

    public int[] delta(int[] inputSet) {
        for (String model : children.keySet()) {
            Model currentModel = children.get(model);
            currentModel.state = currentModel.delta(currentModel.incomingInput);
            if (verbose) {
                String state = "";
                for (int i = 0; i < currentModel.state.length; i++) {
                    state += currentModel.state[i] + " ";
                }
                state = state.substring(0, state.length() - 1);
                System.out.println("New state for " + model + ": {" + state + "}");
            }
        }

        return null;
    }

    public void addModel(String modelName, Model m) {
        this.children.put(modelName, m);
    }

    public void networkInputLink(int [] input, Model receivingModel) {
        receivingModel.incomingInput = input;
    }

    public void networkOutputLink(Model outputtingModel) {
        output = outputtingModel.lambda();
    }

    public void simpleLink(Model sourceModel, Model destinationModel) {
        int sourceOut = sourceModel.lambda();
        int[] destinationModelInput = { sourceOut};
        destinationModel.incomingInput = destinationModelInput;
    }

    public void dualInputLink(Model sourceModel1, Model sourceModel2, Model destinationModel) {
        int model1Out = sourceModel1.lambda();
        int model2Out = sourceModel2.lambda();

        int[] destinationModelInput = { model1Out, model2Out };

        destinationModel.incomingInput = destinationModelInput;
    }

}