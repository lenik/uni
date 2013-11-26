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
    // printf("mode value for %s is %d\n", norm, mode);
    free(copy);

    if (mode & F_DENY) {
        fprintf(stderr, "Execution of %s is denied.\n", norm);
        return -1;
    }

    return next(command);
}
