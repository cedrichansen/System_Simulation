public class Battery extends CarPart{
    static String name = "Battery";

    public Battery(String n) {
        super(n);
    }

    @Override
    public String toString() {
        return name;
    }
}
