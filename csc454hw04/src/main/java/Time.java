public class Time implements Comparable<Time>{
    public double realTime;
    public double discreteTime;

    public Time (double r, double d) {
        realTime = r;
        discreteTime = d;
    }

    @Override
    public int compareTo(Time o) {
        if (o.realTime > this.realTime) {
            return -1;
        } else if (o.realTime == this.realTime) {
            return (int)(o.discreteTime - this.discreteTime);
        } else {
            //other.realTime < this.realTime
            return 1;
        }
    }

    

}