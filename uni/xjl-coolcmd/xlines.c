/**
    You should add following contents to auto tools:

    configure.ac:
        PKG_CHECK_MODULES([GLIB], [glib-2.0])
        AC_HEADER_STDC
        AC_CHECK_HEADERS([stdlib.h])
        AC_CHECK_FUNCS([strchr])
        AC_CHECK_FUNCS([strtol])
        AC_FUNC_ERROR_AT_LINE

    Makefile.am:
        AM_CPPFLAGS = ${GLIB_CFLAGS}
        <program>_LDADD = $(GLIB_LIBS)
 */
#include "config.h"

#define RCS_ID      "$Id$"
#define DESCRIPTION "Render lines with different colors"

#include <cprog.h>

#define MAX_MODES 10
GString *modes[MAX_MODES];
int n_modes = 0;

static GOptionEntry options[] = {
    { "mode",      'm', G_OPTION_STRING,
      G_OPTION_AG_CALLBACK, add_mode,
      "Add a line mode", },

    { "force",     'f', 0, G_OPTION_ARG_NONE, &opt_force,
      "Force to continue", },

    { "quiet",     'q', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "Show less verbose info", },

    { "verbose",   'v', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "Show more verbose info", },

    { "version",   '\0', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, show_version,
      "Show version info", },

    { G_OPTION_REMAINING, '\0', 0, G_OPTION_ARG_FILENAME_ARRAY, &opt_files,
      "FILES", },

    { NULL },
};

int main(int argc, char **argv) {
    if (! boot(&argc, &argv, "FILES"))
        return 1;

    for (int i = 0; i < n_modes; i++) {
        printf("mode %d: %s\n", i, modes[i]->str);
    }

    return process_files(opt_files);
}

int process_file(char *filename, FILE *file) {
    LOG1 printf("Processing file %s\n", filename);
    return 0;
}
