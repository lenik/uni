#include <sys/types.h>
#include <assert.h>
#include <ctype.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <bas/cli.h>
#include <bas/str.h>

/* .section. options */

const char *program_title = "PROGRAM";
const char *program_help_args = "ARGUMENTS";

bool parse_options(GOptionEntry *options, int *_argc, char ***_argv) {
    int argc = *_argc;
    char **argv = *_argv;

    GError *gerr = NULL;
    GOptionContext *opts;
    int err;

    opts = g_option_context_new(program_help_args);
    g_option_context_add_main_entries(opts,
                                      options,
                                      NULL /* translation-domain */
                                      );

    if (! g_option_context_parse(opts, &argc, &argv, &gerr)) {
        log_err("Couldn't parse options: %s", gerr->message);
        return FALSE;
    }

    g_option_context_free(opts);
    *_argc = argc;
    *_argv = argv;
    return TRUE;
}

gboolean _parse_option(const char *opt,
                       const char *val,
                       gpointer data,
                       GError **err) {
    assert(*opt == '-');
    opt++;

    bool shortopt = *opt != '-';
    while (*opt == '-') opt++;

    switch (*opt) {
    case 'e':
        if (streq(opt, "error-continue")) {
            opt_error_continue = true;
            return true;
        }
        break;

    case 'f':
        if (shortopt || streq(opt, "force")) {
            opt_force = true;
            return true;
        }
        break;

    case 'q':
        if (shortopt || streq(opt, "quiet")) {
            log_level--;
            return true;
        }
        break;

    case 'v':
        if (shortopt || streq(opt, "verbose")) {
            log_level++;
            return true;
        }
        if (streq(opt, "version")) {
            puts(program_title);
            puts("Written by Lenik, (at) 99jsj.com");
            return true;
        }
        break;
    }

    log_err("Bad option: %s %s", opt, val);
    return FALSE;
}

/* .section. logging */

bool opt_error_continue = false;
bool opt_force = false;

bool error(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    return false;
}

/* .section. utils */

bool process_files(char **paths,
                   const char *open_mode,
                   file_handler handler,
                   void *data) {
    assert(paths != NULL);
    assert(open_mode != NULL);
    assert(handler != NULL);

    bool err = false;
    const char *path;

    /* empty path list? read from stdin. */
    if (*paths == NULL)
        return handler("<stdin>", stdin, data);

    for (path = *paths; *path; paths++) {
        FILE *in = fopen(path, open_mode);
        if (in == NULL) {
            log_perr("Can't open file %s", path);
            err = true;
        } else {
            if (! handler(path, in, data))
                err = true;
            fclose(in);
        }

        if (err && !opt_error_continue)
            break;
    }

    return err;
}
