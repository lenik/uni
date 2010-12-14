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

#define RCS_ID      "$Id:$"
#define DESCRIPTION "Find the longest matching prefix"

#include <cprog.h>

char *opt_string;
int opt_string_len;
int match_count = 0;

static GOptionEntry options[] = {
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

    { NULL },
};

int main(int argc, char **argv) {
    if (! boot(&argc, &argv, "STRING [FILES]"))
        return 1;

    argv++; argc--;

    if (argc == 0) {
        fprintf(stderr, "String isn't specified. ");
        return 1;
    }

    opt_string = *argv++; argc--;
    opt_string_len = strlen(opt_string);

    /* chomp '/' */
    /*
    if (opt_string_len > 1 && opt_string[opt_string_len - 1] == '/')
        opt_string[--opt_string_len] = '\0';
    */

    LOG2 printf("String: %s\n", opt_string);

    argv[argc] = NULL;
    process_files(argv);
    return match_count ? 0 : 1;
}

int process_file(char *filename, FILE *file) {
    int  lno = 0;
    char linebuf[MAX_LINE];
    int  maxlen = 0;
    char maxcopy[MAX_LINE];
    int  maxlno;

    LOG2 printf("Process %s\n", filename);
    while (fgets(linebuf, MAX_LINE, file) != NULL) {
        char *p;
        int cc;
        int i;

        lno++;
        cc = strlen(linebuf);
        if (cc == MAX_LINE - 1 && linebuf[MAX_LINE - 1] != '\n') {
            fprintf(stderr, "%s:%d: Line too long\n", filename, lno);
            exit(1);
        }

        p = linebuf;
        while (isspace(*p)) p++;

        if (!*p || *p == '#')
            continue;

        if (cc > opt_string_len)
            cc = opt_string_len;

        int maxlen_aligned = 0;
        for (i = 0; i <= cc; i++) {
            char c = p[i];
            char d = opt_string[i];
            if (c != d) {
                LOG3 printf("stop: %s | %s", opt_string+i, p+i);
                if ((c == '\0' || isspace(c)) &&
                    (d == '\0' || d == '/' ||
                        (i >= 1 && opt_string[i - 1] == '/')
                        ))
                    ;
                else if (c == '/' && d == '\0')
                    ;
                else
                    break;

                if (i > maxlen) {
                    LOG2 printf("%s:%d:match %s", filename, lno, p);
                    strcpy(maxcopy, p);
                    maxlen = i;
                    maxlno = lno;
                }
                break;
            }
        }
    }

    if (maxlen == 0) {
        LOG1 printf("%s: No matching prefix\n", filename);
    } else {
        char *s = maxcopy;
        char *t = opt_string;
        s += maxlen;
        t += maxlen;

        if (isspace(*s) || *s == '\0') {
            if (*t == '\0' || *t == '/')
                ;
            else if (s[-1] == '/')
                ;
            else
                s = NULL;
        } else if (*s == '/') {
            if (*t == '\0')
                s++;
            else
                s = NULL;
        } else
            s = NULL;

        // ASSERT s != NULL;
        if (s == NULL) {
            fprintf(stderr, "Bad match: %s -> %s +%d\n", opt_string, maxcopy, maxlen);
            exit(1);
        }

        while (isspace(*s)) s++;

        LOG2 printf("%s:%d:longest-match %s", filename, maxlno, s);
        printf("%s", s);

        match_count++;
    }
    return 0;
}
