#include "posix_fn.h"

int system(const char *command) {
    static int (*next)(const char *);
    def_next(system);

    // pmap_dump();

    char *copy = strdup(command);
    if (copy == NULL)
        return -1;                      /* ENOMEM */

    char *cmd = qstr_btok(copy, NULL);

    NORM_CONFIG(system, cmd);

    free(copy);

    RET_IF_DENY(norm, mode);

    return next(command);
}
