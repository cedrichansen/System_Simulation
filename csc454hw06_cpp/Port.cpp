template <class T>
class Port
{
public:
    T currentValue;

    Port(T initial)
    {
        currentValue = initial;
    }
};