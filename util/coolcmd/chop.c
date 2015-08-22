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

#define RCS_ID      "$Id: - @VERSION@ @DATE@ @TIME@ - $"
#define DESCRIPTION "Remove the CR/LF characters near EOF."
#define YBUF_SIZE   100000

#include <cprog.h>

char opt_char = '\n';

static GOptionEntry options[] = {
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
    int i;

    if (! boot(&argc, &argv, "FILES"))
        return 1;

    return process_files(opt_files);
}

int process_file(char *filename, FILE *file) {
    int c;
    char ybuf[YBUF_SIZE];
    int yp = 0;

    while ((c = fgetc(file)) != EOF) {
        switch (c) {
        case '\n':
        case '\r':
            if (yp >= YBUF_SIZE) {
                error("buffer overflow\n");
                return 1;
            }
            ybuf[yp++] = c;
            continue;
        default:
            if (yp) {
                for (int i = 0; i < yp; i++)
                    putchar(ybuf[i]);
                yp = 0;
            }
            putchar(c);
        }
    }
}
