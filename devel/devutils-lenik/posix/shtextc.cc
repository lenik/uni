#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#ifndef MAX_PATH
#   define MAX_PATH 1024
#endif
#define LINE_MAX 10000

static const char *tmpdir;
static int seq = 1;
static FILE *opentmp(char *path, const char *suffix) {
    if (tmpdir == NULL) {
        tmpdir = getenv("TEMP");
        if (tmpdir == NULL)
            tmpdir = getenv("TMP");
        if (tmpdir == NULL)
            tmpdir = "/tmp";
    }
    pid_t pid = getpid();
    sprintf(path, "%s/%d-%d%s", tmpdir, pid, seq++, suffix);
    FILE *f = fopen(path, "w");
    if (f == NULL) {
        fprintf(stderr, "can't write to file %s: ", path); perror("");
        exit(1);
    }
    return f;
}

static int lineno = 0;
static char linebuf[LINE_MAX + 1];
static int lp = 0;

static void echo(int c) {
    if (c == '\n') {
        int type = 'e';
        linebuf[lp] = 0;
        if (lineno == 1) {
            if (linebuf[0] == '#' && linebuf[1] == '!') {
                // remove #! line
                type = 0;
            }
        }
        if (type == 'e')
            printf("echo \'%s\'\n", linebuf);
        lp = 0;
        return;
    }
    switch (c) {
        // case '\\':
        // case '\"':
    case '\'':
        // linebuf[lp++] = '\\';
        /* ...' "'" '... */
        linebuf[lp++] = '\'';
        linebuf[lp++] = '"';
        linebuf[lp++] = '\'';
        linebuf[lp++] = '"';
        linebuf[lp++] = '\'';
        return;
    case '\r': // skip
        return;
    }
    linebuf[lp++] = c;
    if (lp >= LINE_MAX) {
        linebuf[lp] = 0;
        fprintf(stderr, "line is too long: %s", linebuf);
        exit(1);
    }
}

static void echoflush() {
    if (lp != 0) {
        linebuf[lp] = 0;
        printf("echo -n \'%s\'\n", linebuf);
        lp = 0;
    }
}

#define E_STMT 0
#define E_EVAL 1
static int evalmode = E_STMT;

static int ep = 0;

static void eval(int c) {
    if (ep == 0 && c == '=') {
        evalmode = E_EVAL;
        printf("echo -n ");
    } else {
        // switch (evalmode) { ... }
        putchar(c);
    }
    ep++;
}

static void evalflush() {
    printf("\n");
}

#define S_TEXT 0
#define S_EVAL 2
// ?\> in eval-block are converted to ?>
int parse(FILE *in) {
    int state = S_TEXT;
    int c, look;
    int x;
    c = fgetc(in);
    lineno = 0;
    while (c != EOF) {
        if (c == '\n')
            lineno++;
        switch (state) {
        case S_TEXT:
            if (c == '<') {
                look = fgetc(in);
                if (look == '?') { // <?
                    echoflush();
                    state = S_EVAL;
                    evalmode = E_STMT;
                    ep = 0;
                } else if (look == '\\') { // <\X => <X
                    echo(c);
                    c = fgetc(in);
                    continue;
                } else { // <X
                    echo(c);
                    c = look;
                    continue;
                }
            } else {
                echo(c);
            }
            break;
        case S_EVAL:
            if (c == '?') {
                look = fgetc(in);
                if (look == '>') { // ?>
                    evalflush();
                    state = S_TEXT;
                } else if (look == '\\') { // ?\X => ?X
                    eval(c);
                    c = fgetc(in);
                    continue;
                } else { // ?X
                    eval(c);
                    c = look;
                    continue;
                }
            } else {
                eval(c);
            }
            break;
        default:
            return 1;
        }
        c = fgetc(in);
    }
    if (state != S_TEXT) {
        fprintf(stderr, "eval block wasn't closed");
        return 1;
    }
    echoflush();
    return 0;
}

int main(int argc, char **argv, char **env) {
    while (--argc) {
        char *arg = *++argv;
        if (*arg == '-') {
            switch (*++arg) {
            case 'h':
                printf(
                    "shtext FILE.sht ...\n"
                    );
                return 0;
            default:
                fprintf(stderr, "bad option: %s\n", arg);
                return 1;
            }
        } else {
            char cwd[MAX_PATH];
            if (getcwd(cwd, sizeof(cwd)) == NULL) {
                fprintf(stderr, "can't getcwd.");
                return 1;
            }
            char *slash = strrchr(arg, '/');
            char *bslash = strrchr(arg, '\\');
            if (slash < bslash) slash = bslash;
            if (slash != NULL) {
                char dirname[MAX_PATH];
                strcpy(dirname, arg);
                dirname[slash - arg] = 0;
                if (chdir(dirname) != 0) {
                    fprintf(stderr, "can't chdir to %s: ", dirname); perror("");
                    return 1;
                }
            }
            FILE *f = fopen(arg, "r");
            if (f == NULL) {
                fprintf(stderr, "can't open file %s: ", arg); perror("");
                return 1;
            }
            int result = parse(f);
            fclose(f);
            if (slash != NULL) {
                if (chdir(cwd) != 0) {
                    fprintf(stderr, "can't go back to %s: ", cwd); perror("");
                    return 1;
                }
            }
            if (result != 0) {
                fprintf(stderr, "error parsing %s.", arg);
                return result;
            }
        }
    }
    return 0;
}
