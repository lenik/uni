#include "foo.h"

#include <iostream>
using namespace std;

#ifdef C_PROV

void IFoo::foo() {

    cout << "foo.cc: foo()" << endl;

}

#endif
