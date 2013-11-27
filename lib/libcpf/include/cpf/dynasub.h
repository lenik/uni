#ifndef _dynasub_umac_awRQb7t54BF4GcN3_
#define _dynasub_umac_awRQb7t54BF4GcN3_ "$Id: dynasub.h,v 1.1 2007-07-15 10:52:38 dansei Exp $"

#ifdef DYNALOAD

    #include "assert.h"

    HMODULE _cc
    get_calling_module();

    void * _cc
    load_proc(LPCTSTR lib, LPCTSTR proc);

    #define DYNACALL(lib, proc, rett, cc, form, ret, actual) do { \
            rett (cc *_proc)form; \
            _proc = (rett (cc *)form) load_proc(lib, #proc); \
            if (_proc == 0) { \
                fprintf(stderr, "Fail to load %s/%s\n", lib, #proc); \
                break; \
            } \
            ret = (*_proc)actual; \
        } while (0)

    #define DYNASUB(lib, proc, rett, cc, form, actual) \
        static rett cc proc form { \
            rett retval; \
            DYNACALL(lib, proc, rett, cc, form, retval, actual); \
            return retval; \
        }

    #define DYNACALL_V(lib, proc, cc, form, ret, actual) \
        DYNACALL(lib, proc, int, cc, form, ret, actual)

    #define DYNASUB_V(lib, proc, cc, form, actual) \
        DYNASUB(lib, proc, int, cc, form, actual)

#else

    #define DYNACALL(lib, proc, rett, cc, form, ret, actual) \
        (ret = (rett) (proc actual))

    #define DYNASUB(lib, proc, rett, cc, form, actual) \
        rett cc proc form;

    #define DYNACALL_V(lib, proc, cc, form, ret, actual) \
        (proc actual)

    #define DYNASUB_V(lib, proc, cc, form, actual) \
        void cc proc form;

#endif

#endif
