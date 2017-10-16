#include "config.h"

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

#include <glib.h>

#define MAX_LINE 1024

#define LOG0 if (opt_verbose >= 0)
#define LOG1 if (opt_verbose >= 1)
#define LOG2 if (opt_verbose >= 2)

int         opt_value = INT_MIN;
int         opt_delta = INT_MIN;
int         opt_verbose   = 0;
char **     opt_files;

gboolean error(const char *fmt, ...);

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
    printf("vercomp 0.1\n"
           "written by Lenik, (at) 99jsj.com\n");
    exit(0);
}

static GOptionEntry entries[] = {
    { "value",     'n', 0, G_OPTION_ARG_INT, &opt_value,
      "set the brightness value", },

    { "delta",     'd', 0, G_OPTION_ARG_INT, &opt_delta,
      "increase or decrease the brightness", },

    { "quiet",     'q', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "output less verbose info", },

    { "verbose",   'v', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "output more verbose info", },

    { "version",   '\0', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, show_version,
      "show version info", },

    { G_OPTION_REMAINING, '\0', 0,
      G_OPTION_ARG_FILENAME_ARRAY, &opt_files, "FILES", },

    { NULL },
};

int main(int argc, char **argv) {
    GError *gerr = NULL;
    GOptionContext *opts;

    opts = g_option_context_new("");
    g_option_context_add_main_entries(opts, entries,
                                      NULL /* translation-domain */
                                      );

    if (! g_option_context_parse(opts, &argc, &argv, &gerr)) {
        fprintf(stderr, "Couldn't parse options: %s\n", gerr->message);
        return 1;
    }
    g_option_context_free(opts);

    LOG2 printf("value: %d\n", opt_value);
    LOG2 printf("delta: %d\n", opt_delta);

    const char *ctl = "/sys/class/backlight/intel_backlight/brightness";
    FILE *f = fopen(ctl, "rt");
    if (f == NULL) {
        error("Can't read from %s: ", ctl);
        return 1;
    }

    int oldval;
    fscanf(f, "%d", &oldval);
    fclose(f);
    LOG2 printf("old-value: %d\n", oldval);

    int newval = INT_MIN;
    if (opt_value != INT_MIN) {
        newval = opt_value;
    }
    else if (opt_delta != INT_MIN) {
        newval = oldval + opt_delta;
    }
    else {
        printf("%d\n", oldval);
        return 0;
    }

    LOG2 printf("new-value: %d\n", newval);
    f = fopen(ctl, "wt");
    if (f == NULL) {
        error("Can't write to %s: ", ctl);
        return 1;
    }

    fprintf(f, "%d\n", newval);
    fclose(f);

    return 0;
}

gboolean error(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    perror("");
    return FALSE;
}
