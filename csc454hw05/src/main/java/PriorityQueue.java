import java.util.ArrayList; import java.util.Collections;

public class PriorityQueue <T extends Comparable<T>> {

    ArrayList<T> list = new ArrayList<T>();

    public void add(T object) {
        list.add(object);
        Collections.sort(list);
    }

    public T remove(){
        if (list.size() != 0) {
            return list.remove(0);
        }
        return null;
    }

    public T peek(){
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

}