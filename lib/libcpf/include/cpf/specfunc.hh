#ifndef _specfunc_umac_n_xWDeY3RH9IqA5P_
#define _specfunc_umac_n_xWDeY3RH9IqA5P_

#include "config.h"

/*
  ... {
      _function_begin_(int)(int a, int b) {
          return a + b;
      } _function_end_(add);
      int x = add(3, 4); // x = 7
  } ...
*/
#   define _function_begin_(ret) typedef struct { ret operator()
#   define _function_end_(name)  } _localfunc_##name; _localfunc_##name name;

#   define _staticinit_(name) \
    class _sifunc_##name { \
    public: \
        bool body(); \
        _sifunc_##name() { body(); } \
    } _sifunc_##name##; \
    bool _sifunc_##name::body()

#endif
