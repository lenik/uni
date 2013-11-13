// #include <stdio.h>
#include "posix_fn.h"

FILE *popen(const char *command, const char *type) {
    FILE *(*next)(const char *, const char *);
    def_next(popen);

    char *copy = strdup(command);
    char *cmd = qstr_btok(&copy);

    NORM_CONFIG(cmd);

    // free(copy);
    RET_IF_DENY_(norm, config, NULL);

    return next(command, type);
}
