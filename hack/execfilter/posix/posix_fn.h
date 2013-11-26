#define _GNU_SOURCE

#include <assert.h>
#include <errno.h>
#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <dlfcn.h>
#define def_next(fn) \
    if (!next) \
        next = dlsym(RTLD_NEXT, #fn)

#include "../filter.h"                  /* F_* bits */
#include "../qstr.h"                    /* Utilities to parse the quoted
                                           strings. */
#include "../path.h"                    /* Path normalization. */

/*
  #ifndef FN
  #  error "FN isn't defined. "
  #endif
*/

#ifdef FN
#define __BEGIN__(RT, FN, TV) RT FN(TV) {
#define __BODY__ def_next(fn);
#define __END__ }
#endif

#define RET_IF_DENY(path, mode) RET_IF_DENY_(path, mode, -1)
#define RET_IF_DENY_(path, mode, err)                               \
    do {                                                            \
        if ((mode) & F_DENY) {                                      \
            fprintf(stderr, "Execution of %s is denied.\n", path);  \
            errno = EACCES;                                         \
            return err;                                             \
        }                                                           \
    } while (0)

#define NORM_CONFIG(src, path)                                  \
    char *norm = path_find_norm(path);                          \
    xmode_t mode = get_execution_mode_rec(# src, getpid(), path)
