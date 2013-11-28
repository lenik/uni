#ifndef __CLI_H
#define __CLI_H

#include <stdio.h>
#include <glib.h>

/* stdbool */
typedef _Bool bool;
#define true 1
#define false 0

bool streq(const char *a, const char *b);

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

bool parse_options(GOptionEntry *options, int *_argc, char ***_argv);

gboolean parse_option(const char *opt, const char *val,
                      gpointer data, GError **err);

gboolean _parse_option(const char *opt, const char *val,
                       gpointer data, GError **err);

typedef bool (*file_handler)(const char *path, FILE *in, void *data);

bool process_files(char **paths, const char *open_mode,
                   file_handler handler, void *data);

/* .section. logging */

extern bool opt_error_continue;
extern bool opt_force;
extern int opt_log_level;

#define LOG0 if (opt_log_level >= 0)
#define LOG1 if (opt_log_level >= 1)
#define LOG2 if (opt_log_level >= 2)
#define LOG3 if (opt_log_level >= 3)

bool error(const char *fmt, ...);

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
