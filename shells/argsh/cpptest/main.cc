#include <iostream>
using namespace std;

#include "foo.h"

class MyFoo : public IFoo {
public:
    void hello() override {
        cout << "just hi" << endl;
    }

#if defined(H_PROV) || defined(C_PROV)
#else
    void foo() override {
        cout << "main.cc: foo()" << endl;
    }
#endif
};

int main() {
    MyFoo foo;
    foo.hello();
    foo.foo();
    return 0;
}
