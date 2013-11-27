#ifndef _stl_utim_dePCtWbFr8U8faBH_
#define _stl_utim_dePCtWbFr8U8faBH_ "$Header: /mnt/c/.radiko/.miaj/cvs/usnap/cpf/inc/cpf/test/stl.h,v 1.4 2007-07-15 10:52:56 dansei Exp $"

#include <cpf/dt/raw.h>

typedef struct _stl_token_t stl_token_t;
typedef struct _stl_project_t stl_project_t;

#define STL_MAX_TOKENS 256

struct _stl_token_t {
    char cmd;
    const char *name;
    /* Int, Long, Float, Double, Char, String */
    const char *args;
    int (_cc *func)(stl_project_t *, x64_t *);
};

struct _stl_project_t {
    /* token def */
    stl_token_t tokens[STL_MAX_TOKENS];
    int ntokens;

    /* text script */
    const char *text;
    x32_t user;

    /* cache */
    stl_token_t *cmap[256];              /* ascii -> token */

    /* analyze */
    char *textcpy;
    const char *last_error;
    int total;
    int succeeds;
    int ttotal[STL_MAX_TOKENS];
    int tsucceeds[STL_MAX_TOKENS];

    size_t extra;                   /* extra bytes allocated to this struct */
    /* more user data... */
};

int _cc
stl_test(stl_project_t *project);

void _cc
stl_report(stl_project_t *project);

#endif
