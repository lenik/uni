#include <stdio.h>
#include <stdlib.h>
#include <process.h>

#define d1 if (verb >= 1)
#define d2 if (verb >= 2)

#define TO_BACKSLASH        1       /* slash to back-slash */
#define TO_SLASH            2       /* back-slash to slash */

static int      verb        = 1;
static int      fix_index[100];
static int      fix_value[100];
static int      nfixes      = 0;
static int      fixall      = 0;

int opval(char c) {
    switch (c) {
    case '/':
        return TO_SLASH;
    case '\\':
        return TO_BACKSLASH;
    case '-':
    case '+':
        return 0;
    }
    return 0;
}

int is_number(const char *s) {
    while (*s) {
        if (*s < '0' || *s > '9')
            return 0;
        s++;
    }
    return 1;
}

int fixarg(int argindex) {
    int i;
    for (i = 0; i < nfixes; i++)
        if (fix_index[i] == argindex)
            return fix_value[i];
    if (fixall)
        return fix_value[nfixes];
    return 0;
}

void help() {
    printf("myexec [-h -v -q] [*a *1 *2 ...] [-] command-line\n");
}

int main(int argc, char **argv, char **env) {
    char        lpCmdLine[1000] = "";
    int         i;
    int         ret;

    while (--argc) {
        ++argv;
        if (!strcmp("-v", *argv) || !strcmp("--verbose", *argv))
            verb++;
        else if (!strcmp("-q", *argv) || !strcmp("--quiet", *argv))
            verb--;
        else if (!strcmp("-h", *argv) || !strcmp("--help", *argv))
            { help(); return 0; }
        else if (!strcmp("-", *argv))
            { --argc; ++argv; break; }
        else if (! opval(**argv))
            break;
        else if (!strcmp("a", *argv))
            {
                fixall = 1;
                fix_value[nfixes] = opval(**argv);
            }
        else if (is_number(*argv + 1))
            {
                fix_value[nfixes] = opval(**argv);
                fix_index[nfixes++] = strtol(*argv + 1, NULL, 0);
            }
        else
            {
                fprintf(stdout, "Invalid slashfix option: %s\n", *argv);
                return -1;
            }
    }

    for (i = 0; i < argc; i++) {
        int fix = fixarg(i);
        char *arg = argv[i];
        int j, l = strlen(arg);

        if (*lpCmdLine)
            strcat(lpCmdLine, " ");
        switch (fix & 0xf) {
        case TO_SLASH:
            for (j = 0; j < l; j++) {
                if (arg[j] == '\\')
                    arg[j] = '/';
            }
            break;
        case TO_BACKSLASH:
            for (j = 0; j < l; j++) {
                if (arg[j] == '/')
                    arg[j] = '\\';
            }
            break;
        }
        strcat(lpCmdLine, arg);
    }

    d2 {
        int i;
        for (i = 0; i < nfixes; i++)
            printf("fix[%d] = %d => %d\n", i, fix_index[i], fix_value[i]);
        if (fixall)
            printf("fixall enabled (after %d unmatched)\n", nfixes);
        printf("final cmdline: %s\n", lpCmdLine);
    }

    ret = spawnv(P_WAIT, argv[0], argv);
    if (ret == -1) {
        perror("Error when spawn: ");
        return errno;
    }
    return ret;
}
