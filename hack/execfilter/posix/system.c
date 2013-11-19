#include "posix_fn.h"

int system(const char *command) {
    static int (*next)(const char *);
    def_next(system);

    // pmap_dump();

    char *copy = strdup(command);
    if (copy == NULL)
        return -1;                      /* ENOMEM */

    char *cmd = qstr_btok(copy, NULL);

    NORM_CONFIG(cmd);
    // printf("config value for %s is %d\n", norm, config);
    free(copy);

    if (config & F_DENY) {
        fprintf(stderr, "Execution of %s is denied.\n", norm);
        return -1;
    }

    return next(command);
}
