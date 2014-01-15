#include "posix_fn.h"

/* print the cmdline. */
#define echo_cmdline(...) ((void) 0)
//#define echo_cmdline _echo_cmdline

static void _echo_cmdline(const char *src, const char *file,
                         char *const *argv, char *const *envp) {
    char cmdl[8000] = "";
    int maxlen = sizeof(cmdl) - 1;
    cmdl[maxlen] = '\0';

    char *const *p = argv;
    int i = 0;
    while (*p) {
        if (i++ > 0)
            strncat(cmdl, " ", maxlen);
        strncat(cmdl, "\"", maxlen);
        strncat(cmdl, *p, maxlen);
        strncat(cmdl, "\"", maxlen);
        p++;
    }

    log_debug("%s%c %s", src, envp ? '=' : ':', cmdl);

    if (envp) {
        p = envp;
        while (*p)
            log_debug("  env %s", *p++);
    }
}

static int _execv(const char *path, char *const argv[]) {
    static int (*next)(const char *, char *const *);
    def_next(execv);

    echo_cmdline("execl/v", path, argv, NULL);

    NORM_CONFIG(execv, path);
    RET_IF_DENY(norm, mode);

    int ret = next(path, argv);
    if (ret == -1) {
        log_perr("execv failed");
    }
    return ret;
}

static int _execvp(const char *file, char *const argv[]) {
    static int (*next)(const char *, char *const *);
    def_next(execvp);

    echo_cmdline("execl/vp", file, argv, NULL);

    NORM_CONFIG(execvp, file);
    RET_IF_DENY(norm, mode);

    int ret = next(file, argv);
    if (ret == -1) {
        log_perr("execvp failed");
    }
    return ret;
}

static int _execvpe(const char *file, char *const argv[], char *const envp[]) {
    static int (*next)(const char *, char *const *, char *const *);
    def_next(execvpe);

    echo_cmdline("execl/vpe", file, argv, envp);

    NORM_CONFIG(execvpe, file);
    RET_IF_DENY(norm, mode);

    int ret = next(file, argv, envp);
    if (ret == -1) {
        log_perr("execvpe failed");
    }
    return ret;
}

static int _execve(const char *file, char *const argv[], char *const envp[]) {
    static int (*next)(const char *, char *const *, char *const *);
    def_next(execve);

    echo_cmdline("execl/ve", file, argv, envp);

    NORM_CONFIG(execve, file);
    RET_IF_DENY(norm, mode);

    int ret = next(file, argv, envp);
    if (ret == -1) {
        log_perr("execve failed");
    }
    return ret;
}

int execv(const char *path, char *const argv[]) {
    // log_info("execv %s ...", path);
    return _execv(path, argv);
}

int execvp(const char *file, char *const argv[]) {
    // log_info("execvp %s ...", file);
    return _execvp(file, argv);
}

int execvpe(const char *file, char *const argv[], char *const envp[]) {
    // log_info("execvpe %s ...", file);
    return _execvpe(file, argv, envp);
}

int execve(const char *file, char *const argv[], char *const envp[]) {
    // log_info("execve %s ...", file);
    return _execve(file, argv, envp);
}

#define PTRSIZE sizeof(void *)

static char **va_ptrsz(va_list *vpp, int *countp,
                       bool has0, const char *arg0) {
    int alloc = 16;
    char **ptrv = malloc(alloc * sizeof(char *));
    int count = 0;

    bool more;

    if (has0) {
        if ((more = arg0 != NULL))
            ptrv[count++] = (char *) arg0;
    } else {
        more = true;
    }

    if (more) {
        while (true) {
            char *ptr = va_arg(*vpp, char *);
            if (ptr == NULL)
                break;
            if (count >= alloc - 1) {   /* extra +1 for NUL terminator */
                alloc *= 2;
                ptrv = realloc(ptrv, alloc * sizeof(char *));
                assert(ptrv != NULL);
            }
            ptrv[count++] = ptr;
        }
    }

    ptrv[count] = NULL;

    if (countp)
        *countp = count;
    return ptrv;
}

/* p for PATH-search, e for environ:
   execl, execlp, execle, execlpe, execv, execvp, execvpe, execve */

int execl(const char *path, const char *arg0, ...) {
    // log_info("execl %s ...", path);

    va_list vp;
    va_start(vp, arg0);
    char **argv = va_ptrsz(&vp, NULL, true, arg0);
    va_end(vp);

    int exit;
    exit = _execv(path, argv);
    free(argv);
    return exit;
}

/* Execute FILE, searching in the `PATH' environment variable if it contains no
   slashes, with all arguments after FILE until a NULL pointer and environment
   from `environ'.  */
int execlp(const char *file, const char *arg0, ...) {
    // log_info("execlp %s ...", path);

    va_list vp;
    va_start(vp, arg0);
    char **argv = va_ptrsz(&vp, NULL, true, arg0);
    va_end(vp);

    int exit;
    exit = _execvp(file, argv);
    free(argv);
    return exit;
}

/* Execute PATH with all arguments after PATH until a NULL pointer, and the
   argument after that for environment.  */
int execle(const char *path, const char *arg0, ...) {
    // log_info("execle %s ...", path);

    int argc;

    va_list vp;
    va_start(vp, arg0);
    char **argv = va_ptrsz(&vp, &argc, true, arg0);
    char **envv = va_arg(vp, char **);
    va_end(vp);

    /*
    int i;
    for (i = 0; i < argc; i++)
        log_debug("  arg %d: %s\n", i, argv[i]);

    if (envv) {
        char **p = envv;
        while (*p) {
            log_debug("  env: %s\n", *p);
            p++;
        }
    }
    */

    int exit;
    exit = _execve(path, argv, envv);
    free(argv);
    return exit;
}
