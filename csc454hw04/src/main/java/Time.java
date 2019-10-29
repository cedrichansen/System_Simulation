import java.util.Objects;

public class Time implements Comparable<Time> {
    public final double realTime;
    public final int discreteTime;

    public Time (double r, int d) {
        realTime = r;
        discreteTime = d;
    }

    @Override
    public int compareTo(Time o) {
        if (o.realTime > this.realTime) {
            return -1;
        } else if (o.realTime < this.realTime) {
            return 1;
        } else {
            //times are equal 
            return (this.discreteTime - o.discreteTime);
        }
    }

    public boolean equals(Time o) {
        return this.realTime == o.realTime;
    }

    @Override
    public int hashCode() {
     //   return Double.valueOf(realTime).hashCode() ^ Integer.valueOf(discreteTime).hashCode();
     return Objects.hash(realTime, discreteTime);
    }

    public boolean greaterThan(Time o) {
        return this.compareTo(o) >= 1;
    }

    public Time timeAdvance(Time advanceBy) {
        if (this.equals(advanceBy)) {
            return new Time(this.realTime, this.discreteTime + advanceBy.discreteTime);
        } else {
            return new Time(round(this.realTime + advanceBy.realTime), 0);
        }
    }


    public Time since(Time older) {
        return new Time(round(this.realTime- older.realTime), this.discreteTime - older.discreteTime);
    }

    /** Rounds v to nearest hundredth*/
    public double round(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    public String toString(){
        return "(" + realTime + "," + discreteTime + ")";
    }

    

}