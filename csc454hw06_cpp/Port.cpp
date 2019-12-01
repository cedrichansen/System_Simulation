#ifndef PORT
#define PORT

template <class T>
class Port
{
public:
    T currentValue;

    Port(T initial)
    {
        currentValue = initial;
    }

    Port (){
        currentValue = NULL;
    }
};

#endif