#include <assert.h>
#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#include <builtins.h>
#include <builtins/bashgetopt.h>
#include <shell.h>

#include "log.h"

#define LINEMAX     8000

typedef enum _MATH_FN {
    MIN,
    MAX,
    AVG,
    SUM
} MATH_FN;

int math_builtin(WORD_LIST *list, MATH_FN fn) {
    int opt;
    bool isfile = false;
    FILE *in;
    char buf[(LINEMAX + 1) * 2];
    int rval = EXECUTION_SUCCESS;
    int index = 0;
    int count = 0;
    long n;
    char *endptr = NULL;
    long min;
    long max;
    long sum = 0;
    long avg;
    
    reset_internal_getopt();
    while ((opt = internal_getopt(list, "f")) != -1) {
        switch (opt) {
        case 'f':
            isfile = 1;
            break;
        default:
            builtin_usage();
            return EX_USAGE;
        }
    }
    if ((list = loptend) == NULL) {
        builtin_usage();
        return EX_USAGE;
    }


    for (; list; index++, list = list->next) {
        const char *arg = list->word->word;

        if (isfile) {                   /* file argument */
            log_debug("Process file %s", arg);

            in = fopen(arg, "r");
            if (in == NULL) {
                log_perr("Failed to open %s", arg);
                rval = EXECUTION_FAILURE;
                break;
            }

            while (fgets(buf, LINEMAX, in)) {
                n = strtol(buf, &endptr, 0);
                if (n < min || count == 0)
                    min = n;
                if (n > max || count == 0)
                    max = n;
                sum += n;
                count++;
            }
            
            fclose(in);
        } else {                        /* string argument */
            n = strtol(arg, &endptr, 0);
            if (n < min || count == 0)
                min = n;
            if (n > max || count == 0)
                max = n;
            sum += n;
            count++;
        }
    }
    avg = sum / count;

    switch (fn) {
        case MIN:
            printf("%d\n", min);
            break;
        case MAX:
            printf("%d\n", max);
            break;
        case AVG:
            printf("%d\n", avg);
            break;
        case SUM:
            printf("%d\n", sum);
            break;
    }
    return rval;
}

int min_builtin(WORD_LIST *list) {
    math_builtin(list, MIN);
}

int max_builtin(WORD_LIST *list) {
    math_builtin(list, MAX);
}

int avg_builtin(WORD_LIST *list) {
    math_builtin(list, AVG);
}

int sum_builtin(WORD_LIST *list) {
    math_builtin(list, SUM);
}

char *min_doc[] = {
    "Get the minimum number",
    "",
    "Find and print the minimum value from the arguments (or files).",
    NULL
};

char *max_doc[] = {
    "Get the maximum number",
    "",
    "Find and print the maximum value from the arguments (or files).",
    NULL
};

char *avg_doc[] = {
    "Get the average value",
    "",
    "Print the average value from the arguments (or files).",
    NULL
};

char *sum_doc[] = {
    "Get the sum",
    "",
    "Print the sum value from the arguments (or files).",
    NULL
};

struct builtin min_struct = {
    "min",
    min_builtin,
    BUILTIN_ENABLED,
    min_doc,
    "min [-f] { FILE | <num ...> }",
    NULL
};

struct builtin max_struct = {
    "max",
    max_builtin,
    BUILTIN_ENABLED,
    max_doc,
    "max [-f] { FILE | <num ...> }",
    NULL
};

struct builtin avg_struct = {
    "avg",
    avg_builtin,
    BUILTIN_ENABLED,
    avg_doc,
    "avg [-f] { FILE | <num ...> }",
    NULL
};

struct builtin sum_struct = {
    "sum",
    sum_builtin,
    BUILTIN_ENABLED,
    sum_doc,
    "sum [-f] { FILE | <num ...> }",
    NULL
};
