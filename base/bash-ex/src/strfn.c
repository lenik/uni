#include <assert.h>
#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>

#include <builtins.h>
#include <builtins/bashgetopt.h>
#include <shell.h>

#include "log.h"

#define LINEMAX     8000

/* TODO UTF-8 Support */
typedef void str_proc(char *buf);

int strfn_builtin(WORD_LIST *list, str_proc fn, const char *fsep, const char *asep) {
    int opt;
    bool isfile = false;
    FILE *in;
    char buf[(LINEMAX + 1) * 2];
    int rval = EXECUTION_SUCCESS;
    int index = 0;
    
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

            if (index) fputs(fsep, stdout);
            
            in = fopen(arg, "r");
            if (in == NULL) {
                log_perr("Failed to open %s", arg);
                rval = EXECUTION_FAILURE;
                break;
            }

            while (fgets(buf, LINEMAX, in)) {
                fn(buf);
                fputs(buf, stdout);
            }
            
            fclose(in);
        } else {                        /* string argument */
            if (index) fputs(asep, stdout);

            strcpy(buf, arg);
            fn(buf);

            fputs(buf, stdout);
        }
    }

    if (index)
        putchar('\n');
    
    return rval;
}

void tolower_fn(char *buf) {
    char *p = buf;
    char ch;
    while (ch = *p) {
        ch = tolower(ch);
        *p++ = ch;
    }
}

void toupper_fn(char *buf) {
    char *p = buf;
    char ch;
    while (ch = *p) {
        ch = toupper(ch);
        *p++ = ch;
    }
}

int tolower_builtin(WORD_LIST *list) {
    strfn_builtin(list, tolower_fn, "\n", " ");
}

int toupper_builtin(WORD_LIST *list) {
    strfn_builtin(list, toupper_fn, "\n", " ");
}

char *tolower_doc[] = {
    "Convert to lower case",
    "",
    "Convert the argument string (or file) to lower case.",
    NULL
};

char *toupper_doc[] = {
    "Convert to upper case",
    "",
    "Convert the argument string (or file) to upper case.",
    NULL
};

struct builtin tolower_struct = {
    "tolower",
    tolower_builtin,
    BUILTIN_ENABLED,
    tolower_doc,
    "tolower [-f] string/file ...",
    NULL
};

struct builtin toupper_struct = {
    "toupper",
    toupper_builtin,
    BUILTIN_ENABLED,
    toupper_doc,
    "toupper [-f] string/file ...",
    NULL
};
