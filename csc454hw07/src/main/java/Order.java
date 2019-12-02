public class Order {

    int numberOfTires;
    int numberOfBatteries;
    int numberOfEngines;

    public Order(int nT, int nB, int nE) {
        this.numberOfBatteries = nB;
        this.numberOfEngines = nE;
        this.numberOfTires = nT;
    }


    public String toString(){
        return "Tires: " + this.numberOfTires + " Batteries: " + this.numberOfBatteries + " Engines: " + this.numberOfEngines ;
    }

}
