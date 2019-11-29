template <class T> class Port {

    T * currentValue;

     Port(T * initial) {
        currentValue = initial;
    }

};