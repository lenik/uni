#include <iostream>

#define H_PROV

//#define H_SEP

class IFoo {
public:
    virtual void hello() = 0;
#if defined(C_PROV)
    virtual void foo();
#elif defined(H_PROV)

#   ifdef H_SEP
        virtual void foo();
#   else
        virtual void foo() {
            std::cout << "foo.h: foo()" << std::endl;
        }
#   endif
#else
    virtual void foo() = 0;
#endif
};

#if defined(H_PROV) && defined(H_SEP)
void IFoo::foo() {
    std::cout << "foo.h: foo()" << std::endl;
}
#endif
