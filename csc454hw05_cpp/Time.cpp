#ifndef TIME
#define TIME

class Time {
    public:
     double realTime;
     int discreteTime;

    Time (double r, int d) {
        realTime = r;
        discreteTime = d;
    }
    Time(){
        realTime = 0;
        discreteTime = 0;
    }

    bool equals(Time o) {
        return realTime == o.realTime;
    }

    int hashCode() {
        return 51 ^ (int)realTime >> 31 * discreteTime;
    }

    Time * timeAdvance(Time advanceBy) {
        if (equals(advanceBy)) {
            return new Time(realTime, discreteTime + advanceBy.discreteTime);
        } else {
            return new Time(round(realTime + advanceBy.realTime), 0);
        }
    }


    Time * since(Time older) {
        return new Time(round(realTime- older.realTime), discreteTime - older.discreteTime);
    }

    /** Rounds v to nearest hundredth*/
    double round(double v) {
        return round(v * 10000.0) / 10000.0;
    }
   

};

#endif