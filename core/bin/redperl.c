
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdarg.h>
#include <process.h>
#include <errno.h>

char ** env;
int     pathc           = 0;
char *  pathv[1024]     = { 0, };
char *  opt_ext         = "pl";
char *  opt_interpreter = "perl";
int     opt_debug       = 0;
int     opt_pause       = 0;
int     opt_quote       = 1;

void init_path() {
    char **e = env;
    char *path = 0;
    while (*e) {
        char *v = *e++;
        char *eq = strchr(v, '=');
        if (_strnicmp(v, "path", eq - v) == 0) {
            path = strdup(++eq);
            break;
        }
    }
    if (path) {
        char *dir = strtok(path, ";");
        while (dir) {
            pathv[pathc++] = strdup(dir);
            dir = strtok(0, ";");
        }
        pathv[pathc] = 0;
        free(path);
    }
}

void parse_option(char *opt) {
    int prefix = tolower(*opt);
    int d = islower(prefix) ? 1 : -1;
    switch (prefix) {
    case 'e':
        opt_ext = ++opt; break;
    case 'i':
        opt_interpreter = ++opt; break;
    case 'd':
        opt_debug += d; break;
    case 'p':
        opt_pause += d; break;
    case 'q':
        opt_quote += d; break;
    default:
        printf("unknown option: %c", *opt);
    }
}

char *path_join(char *path, const char *add) {
    int l = strlen(path);
    char last = path[l - 1];
    if (! (last == '/' || last == '\\' || last == ':'))
        strcpy(path + l, "/");
    strcat(path, add);
    return path;
}

char *find_path(char *name) {
    char **p = pathv;
    char buf[1024];
    while (*p) {
        strcpy(buf, *p);
        path_join(buf, name);
        if (_access(buf, 0) == 0)
            return *p;
        p++;
    }
    return 0;
}

char *quote(char *buf, char *s) {
    int need = 0;
    char *p = s;
    char c;
    while (c = *p++)
        if (c <= 32) { need = 1; break; }
    if (need) {
        p = buf;
        *p++ = '"';
        while (c = *s++) {
            if (c < 32) {
                p += sprintf(p, "\\x%02x", c);
            }
            *p++ = c;
        }
        *p++ = '"';
        *p++ = 0;
    } else {
        strcpy(buf, s);
    }
    return buf;
}

void help() {
    printf("redperl: perl-redirector, for lanuching perl scripts\n"
           "written by lenik, 2007\n"
           "\n"
           "syntax: \n"
           "    <redperl-alias> <parameters-for-perl>\n"
           "alias-format: \n"
           "    <target-perl-name> [,options [,options ...]] \n"
           "        target perl should be existed in PATH variable\n"
           "option (name is case-insensitive: \n"
           "    E<extension>    default pl\n"
           "    I<interpreter>  default perl, must exist in PATH variable\n"
           "    D               debug mode\n"
           );
}

int main(int argc, char **argv, char **_env) {
    char *prog = strdup(argv[0]);
    char red_name[1024];
    char red_prog[1024];
    char red_argbuf[4096];
    char *pbuf = red_argbuf;
    const char *red_args[100]; /* (const char *const *) */
    char *ext = strrchr(prog, '.');
    char *p = prog;
    int i, c;
    int ret;

    env = _env;

    init_path();

    if (ext) *ext = 0;
    while (p = strrchr(prog, ',')) {
        *p++ = 0;
        parse_option(p);
    }

    if (opt_debug) {
        char **_pathv = pathv;
        while (*_env) printf("env: %s\n", *_env++);
        while (*_pathv) printf("path: %s\n", *_pathv++);
    }

    if (opt_debug) {
        printf("interpreter: %s\n", opt_interpreter);
        printf("prog_name:   %s\n", prog);
        printf("prefer_ext:  %s\n", opt_ext);
        printf("argc:        %d\n", argc);
        for (i = 0; i < argc; i++) {
            printf("argv[%2d]:    %s\n", i, argv[i]);
        }
    }

    sprintf(red_name, "%s.%s", prog, opt_ext);
    if (_access(red_name) == 0) {
        strcpy(red_prog, red_name);
    } else {
        p = find_path(red_name);
        if (p) {
            strcpy(red_prog, p);
            path_join(red_prog, red_name);
        } else {
            printf("target isn't existed");
            return -1;
        }
    }
    if (opt_debug) {
        printf("red_prog:    %s\n", red_prog);
    }

    red_args[0] = quote(pbuf, opt_interpreter);
        pbuf += strlen(pbuf) + 1;
    red_args[1] = quote(pbuf, red_prog);
        pbuf += strlen(pbuf) + 1;
    for (i = 1; i < argc; i++) {
        red_args[i + 1] = quote(pbuf, argv[i]);
        pbuf += strlen(pbuf) + 1;
    }
    red_args[argc + 1] = 0;

    if (opt_debug) {
        for (i = 0; i < argc + 1; i++) {
            printf("red_arg[%2d]: %s\n", i, red_args[i]);
        }
    }

    if (opt_pause) {
        printf("press any key to exec, or ctrl-c to exit...\n");
        c = getch();
        if (c == 3) {
            printf("canceled\n");
            return 0;
        }
        printf("executing %s %s...\n", opt_interpreter, red_prog);
    }

    ret = _execvp(opt_interpreter, red_args);

    if (opt_debug) {
        printf("execvp error: %d\n", ret);
    }

    if (opt_pause) {
        printf("press any key to exit...\n");
        getch();
    }
}
