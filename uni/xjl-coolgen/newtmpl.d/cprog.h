#ifndef __CPROG_H
#define __CPROG_H
#define __CPROG_ID "$Id$"

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <sys/types.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

#include <glib.h>

#define MAX_LINE 4096

#define LOG0 if (opt_verbose >= 0)
#define LOG1 if (opt_verbose >= 1)
#define LOG2 if (opt_verbose >= 2)
#define LOG3 if (opt_verbose >= 3)

gboolean    opt_force       = FALSE;
#define FORCE(expr) \
    do { \
        int err = expr; \
        if (err != 0) { \
            if (! opt_force) return err; \
        } \
    } while (0)

int         opt_verbose     = 1;
char **     opt_files;

char *      _file_ = NULL;
int         _line_ = 0;

gboolean error(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    return FALSE;
}

gboolean parse_error(const char *fmt, ...) {
    va_list ap;
    fprintf(stderr, "%s:%d:", _file_, _line_);
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    return FALSE;
}

gboolean set_verbose_arg(const char *opt,
                         const char *val,
                         gpointer data,
                         GError **err) {
    while (*opt == '-') opt++;
    if (*opt == 'q') {                  /* q, quiet */
        opt_verbose--;
    } else if (*opt == 'v') {           /* v, verbose */
        opt_verbose++;
    }
    return TRUE;
}

void show_version(const char *opt, const char *val,
                  gpointer data, GError **err) {
    printf(DESCRIPTION "\n"
           "Written by Lenik, (at) 99jsj.com\n");
    exit(0);
}

static GOptionEntry options[];

gboolean boot(int *_argc, char ***_argv, const char *args_help) {
    int argc = *_argc;
    char **argv = *_argv;

    GError *gerr = NULL;
    GOptionContext *opts;
    int err;

    opts = g_option_context_new(args_help);
    g_option_context_add_main_entries(opts, options,
                                      NULL /* translation-domain */
                                      );

    if (! g_option_context_parse(opts, &argc, &argv, &gerr)) {
        fprintf(stderr, "Couldn't parse options: %s\n", gerr->message);
        return FALSE;
    }

    g_option_context_free(opts);
    *_argc = argc;
    *_argv = argv;
    return TRUE;
}

int process_file(char *filename, FILE *file);

int process_files(char **files) {
    if (files == NULL || *files == NULL) {
        return process_file(_file_ = "<stdin>", stdin);
    } else {
        int i;
        int err;
        while (_file_ = *files++) {
            _line_ = 0;
            FILE *f = fopen(_file_, "r");
            if (f == NULL) {
                fprintf(stderr, "Can't open file %s to read", _file_);
                if (! opt_force)
                    return 1;
            } else {
                err = process_file(_file_, f);
                fclose(f);
                if (err) {
                    LOG2 fprintf(stderr, "Failed to process file %s.", _file_);
                    if (! opt_force)
                        return err;
                }
            }
        }
    }
}

#endif
