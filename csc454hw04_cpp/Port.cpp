#ifndef PORT
#define PORT
class Port
{
public:
    int currentValue;

    Port()
    {
        currentValue = 0;
    }

    void setValue(int val) {
        currentValue = val;
    }
    
};
#endif