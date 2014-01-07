#include <stdio.h>

#include <builtins.h>
#include <builtins/bashgetopt.h>

int tolower_builtin(WORD_LIST *options) {
    return EXECUTION_SUCCESS;
}

static char **tolower_doc = {
    "tolower",
    NULL
};

struct builtin tolower = {
    "tolower",
    "tolower",
    NULL
};

