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
#define DESCRIPTION "Find the longest matching prefix"

/* #include <cprog.h> */
#include <stdio.h>
#include <stdlib.h>

void show_help() {
    printf("find-prefix STRING [FILES]\n");
}

void find(const char *s, FILE *in) {
    printf("Find %s...\n", s);
}

int main(int argc, char **argv) {
    char *s;
    FILE *f;

    argv++; argc--;

    if (argc < 1 || strcmp("-h", argv[0]) == 0) {
        show_help();
        return 1;
    }

    s = *argv++; argc--;

    while (argc > 0) {
        char filename = *argv++; argc--;
        f = fopen(filename, "r");
        if (f == NULL) {
            fprintf("Can't open file %s: ", filename);
            perror("");
            return 1;
        }
        find(s, f);
        fclose(f);
    }

    return 0;
}
