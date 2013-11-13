#include "posix_fn.h"

void __spawn_compat(const char *cmdline) {
    void (*next)(const char *);
    // def_next(spawn);

    // spawn() isn't part of POSIX.
}
