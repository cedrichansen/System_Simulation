import java.util.LinkedList;
import java.util.Queue;


/**
 *
 * The purpose of this class is to convert the raw input into the network, of form "4-2-1" where 4 represents the amount of tires,
 * 2 represents the amount of batteries, and 1 is amount of engines, convert this into an order, and send it off to the
 * manufacturers who will create the appropriate amount of pieces
 */
public class OrderProcessor extends  Model<String, Order> {

    final static int TIME_TO_PROCESS_ORDER = 1;
    double timeRemainingOnOrder;

    Queue<Order> orders;

    public OrderProcessor(Port<String> in, Port<Order> out){
        numberOfPartsToProcess = 0;
        this.numberOfInputs = 1;
        this.in = new Port[numberOfInputs];
        this.in[0] = in;
        this.out = out;
        lastKnownTime = new Time(0,0);
        orders = new LinkedList<>();
    }


    @Override
    public String lambda() {
        this.out.currentValue = orders.remove();
        numberOfPartsToProcess--;
        return "Order finished processing! Will be making " + this.out.currentValue.toString();
    }

    @Override
    public void externalTransition(Time currentTime, String in) {
        if (numberOfPartsToProcess > 0) {
            //we received a piece, but we are already working on one.. decrement time appropriately
            Time elapsed = new Time(currentTime.realTime - lastKnownTime.realTime, 0);
            timeRemainingOnOrder -= elapsed.realTime;
        } else {
            //we are starting our first piece, set time to processing time...
            timeRemainingOnOrder = TIME_TO_PROCESS_ORDER;
        }

        /*Format specifier for Orderprocessor:
        4-2-1 where 4 represents the amount of tires, 2 represents the amount of batteries, and 1 is amount of engines*/
        String [] ins = in.split("-");
        Order o = new Order(Integer.parseInt(ins[0]), Integer.parseInt(ins[1]), Integer.parseInt(ins[2]));
        orders.add(o);
        lastKnownTime = currentTime;
    }

    @Override
    public void internalTransition() {
        this.lastKnownTime = new Time(this.lastKnownTime.realTime + timeRemainingOnOrder, 0); // we might care to know the time at which processing time was reset
        this.timeRemainingOnOrder = TIME_TO_PROCESS_ORDER;
    }

    @Override
    public void confluentTransition(Time currentTime, String in) {
        internalTransition();
        lastKnownTime = currentTime; //this is to prevent the press from thinking it is already done another part, since internal transition reset time
        externalTransition(currentTime, in);
    }

    @Override
    public Time timeAdvance() {
        if (orders.size() == 0) {
            return new Time(Double.MAX_VALUE, 0);
        } else {
            return new Time(timeRemainingOnOrder, 0);
        }
    }

    @Override
    public double getMaxTimeAdvance() {
        return Double.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "OrderProcessor - Orders in progress: " + this.numberOfPartsToProcess;
    }
}
