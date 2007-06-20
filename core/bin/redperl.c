
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
    switch (tolower(*opt)) {
    case 'e':
        opt_ext = ++opt; break;
    case 'i':
        opt_interpreter = ++opt; break;
    case 'd':
        opt_debug++; break;
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
    if (_access(name) == 0) {
        return ".";
    }
    return 0;
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
    char *red_args[100];
    char *ext = strrchr(prog, '.');
    char *p = prog;
    int i;

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
    p = find_path(red_name);
    if (p) {
        strcpy(red_prog, p);
        path_join(red_prog, red_name);
    } else {
        printf("target isn't existed");
        return -1;
    }
    if (opt_debug) {
        printf("red_prog:    %s\n", red_prog);
    }

    red_args[0] = opt_interpreter;
    red_args[1] = red_prog;
    for (i = 1; i < argc; i++)
        red_args[i + 1] = argv[i];
    red_args[argc + 1] = 0;

    return _execvp(opt_interpreter, red_args);
}
