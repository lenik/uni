#ifndef __XS_UTIL
#define __XS_UTIL

#include <cpf/cpputil.h>

#ifdef CC
#error  macro CC (for chained calling) has been defined
#endif

#define CC \
    do { \
        SPAGAIN; \
        SV *tmp = POPs; \
        PUSHMARK(SP); \
        XPUSHs(tmp); \
        PUTBACK; \
    } while (0)

#ifdef TYPEMAP
#error  macro TYPEMAP (for generating type castors) has been defined
#endif

#define TYPEMAP(type)

/* The global SP isn't changed,
   however the argstack pushed by caller may be overwritten,
   so a local-stack is introduced. */
static SV *_TYPEMAP_OUT(void *var, const char *f, const char *pkg) {
    SV *arg;
    char fbuf[200];
    dSP;

    if (pkg != 0 && strlen(pkg) > 0) {
        sprintf(fbuf, "%s::%s", pkg, f);
        f = fbuf;
    }

    PUSHSTACK;

    PUSHMARK(SP);
    XPUSHs(sv_2mortal(newSViv(PTR2IV(var))));
    PUTBACK;
    call_pv(f, G_SCALAR);
    SPAGAIN;
    arg = POPs;

    POPSTACK;
    // PUTBACK;
    return arg;
}

#define TYPEMAP_OUT(var, type, pkg) \
    _TYPEMAP_OUT(var, QUOTE(_typemap_##type##_output), pkg)

#endif
