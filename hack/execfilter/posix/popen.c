// #include <stdio.h>
#include "posix_fn.h"

FILE *popen(const char *command, const char *type) {
    static FILE *(*next)(const char *, const char *);
    def_next(popen);

    char *copy = strdup(command);
    char *cmd = qstr_btok(copy, NULL);

    NORM_CONFIG(popen, cmd);

    free(copy);
    RET_IF_DENY_(norm, mode, NULL, NULL);

    return next(command, type);
}
