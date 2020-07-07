#ifndef __BAS_CLI_H
#define __BAS_CLI_H

#include <sys/types.h>
#include <assert.h>
#include <errno.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <glib.h>
#include <bas/log.h>
#include <bas/str.h>

/* .section. options */

#define OPTION(shortopt, longopt, description)  \
    { longopt, shortopt,                        \
            G_OPTION_FLAG_NO_ARG,               \
            G_OPTION_ARG_CALLBACK,              \
            &parse_option,                      \
            description,                        \
            }

#define OPTARG(shortopt, longopt, description)  \
    { longopt, shortopt,                        \
            0,                                  \
            G_OPTION_ARG_CALLBACK,              \
            &parse_option,                      \
            description,                        \
            }

extern const char *program_title;
extern const char *program_help_args;

extern bool opt_error_continue;
extern bool opt_force;

bool parse_options(GOptionEntry *options, int *_argc, char ***_argv);

gboolean parse_option(const char *opt, const char *val,
                      gpointer data, GError **err);

gboolean _parse_option(const char *opt, const char *val,
                       gpointer data, GError **err);

typedef bool (*file_handler)(const char *path, FILE *in, void *data);

bool process_files(char **paths, const char *open_mode,
                   file_handler handler, void *data);

/* .sectino. utils */

/* Evaluate and see if the return value matches the given value, if matched,
   return the returned value. Unless opt_force is set to true. */
#define F_EVAL_RET_EQ(expr, chk)                \
    do {                                        \
        int _retval = expr;                     \
        if (_retval == (chk)) {                 \
            if (! opt_force) return _retval;    \
        }                                       \
    } while (0)

/* Evaluate and see if the return value matches the given value, if unmatched,
   return the returned value. Unless opt_force is set to true. */
#define F_EVAL_RET_NEQ(expr, chk)               \
    do {                                        \
        int _retval = expr;                     \
        if (_retval != (chk)) {                 \
            if (! opt_force) return _retval;    \
        }                                       \
    } while (0)

#define F_EVAL_RETZ(expr) F_EVAL_RET_EQ(expr, 0)
#define F_EVAL_RETNZ(expr) F_EVAL_RET_NEQ(expr, 0)

#endif
