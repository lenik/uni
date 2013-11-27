#ifndef _dllsub_umac_awRQb7t54BF4GcN3_
#define _dllsub_umac_awRQb7t54BF4GcN3_ "$Id: dllsub.h,v 1.1 2007-07-14 14:12:52 dansei Exp $"

#ifdef DYNALOAD

    #include "assert.h"

    #define DLLCALL(lib, proc, rett, cc, form, ret, actual) do { \
            HMODULE hlib = LoadLibrary(lib); \
            rett (cc *_proc)form; \
            _assert_(hlib != 0); \
            _proc = (rett (cc *)form) GetProcAddress(hlib, #proc); \
            _assert_(_proc != 0); \
            ret = (*_proc)actual; \
            FreeLibrary(hlib); \
        } while (0)

    #define DLLSUB(lib, proc, rett, cc, form, actual) \
        static rett cc proc form { \
            rett retval; \
            DLLCALL(lib, proc, rett, cc, form, retval, actual); \
            return retval; \
        }

#else

    #define DLLCALL(lib, proc, rett, cc, form, ret, actual) \
        (ret = (rett) (proc actual))

    #define DLLSUB(lib, proc, rett, cc, form, actual) \
        rett cc proc form;

#endif

#endif
