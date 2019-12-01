#ifndef XOR
#define XOR

#include "Model.cpp"
#include "Time.cpp"
#include "Port.cpp"

using namespace std;
class XORModel : public Model<int, int> {

public :

    int* state;

    XORModel(Port<int>* inp, Port<int> * outp) {
        state = new int[1];
        numberOfInputs = 2;
        in = new Port<int>*[numberOfInputs];
        in[0] = inp;
        out = outp;
        lastKnownTime = new Time(0, 0);
    }


    string lambda() {
        out->currentValue = state[0];
        return to_string(out->currentValue);
    }

    void externalTransition(Time * elapsedTime, string inp) {

        bool inputsAreReady = true;
        bool haveInput = !(inp.compare("") == 0);


        if (haveInput) {
            //actually put the input somewhere
            for (int i =0; i<numberOfInputs; i++) {
                if (in[i]->currentValue == NULL) {
                    //give a port the value
                    in[i]->currentValue = stoi(inp);
                    break;
                }
            }
        }


        for (int i = 0; i < numberOfInputs; i++) {
            if (in[i]->currentValue == NULL) {
                //someone set the value
                inputsAreReady = false;
                break;
            }
        }

        if (inputsAreReady) {
            state[0] = in[0]->currentValue ^ in[1]->currentValue;

            //reset inputs
            for (int i = 0; i < numberOfInputs; i++) {
                in[i]->currentValue = NULL;
            }

        } else {
            //System.out.println("Inputs are not ready for xor model");
        }


    }

    void internalTransition() {
        out->currentValue = NULL;
    }

    void confluentTransition(Time * currentTime, string in) {
        internalTransition();
        lastKnownTime->realTime = currentTime->realTime;
        lastKnownTime->discreteTime = currentTime->discreteTime;//this is to prevent the press from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    Time * timeAdvance() {
        return new Time(1, 0);
    }

     double getMaxTimeAdvance() {
        return DBL_MAX;;
    }

    string toString() {
        return "XOR Model - state : " + to_string(state[0]);
    }
};

#endif