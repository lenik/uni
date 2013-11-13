#include "posix_fn.h"

#define PTRSIZE sizeof(void *)

#define va_dup(arg0, envf)                                      \
    int argc = 1;                                               \
    int envc = 0;                                               \
    char **argv;                                                \
    char **envv = NULL;                                         \
                                                                \
    va_list va;                                                 \
    va_start(va, arg0);                                         \
    while (va_arg(va, const char *) != NULL)                    \
        argc++;                                                 \
    if (envf)                                                   \
        while (va_arg(va, const char *) != NULL)                \
            envc++;                                             \
    va_end(va);                                                 \
                                                                \
    argv = (char **) malloc(PTRSIZE * (argc + 1));              \
    if (envf)                                                   \
        envv = (char **) malloc(sizeof(char *) * (envc + 1));   \
                                                                \
    int argi = 0;                                               \
    argv[argi++] = (char *) arg0;                               \
                                                                \
    va_start(va, arg0);                                         \
    const char *arg;                                            \
    while ((arg = va_arg(va, const char *)) != NULL)            \
        argv[argi++] = (char *) arg;                            \
    assert(argi == argc);                                       \
    argv[argc] = NULL;                                          \
                                                                \
    if (envf) {                                                 \
        int envi = 0;                                           \
        while ((arg = va_arg(va, const char *)) != NULL)        \
            envv[envi++] = (char *) arg;                        \
        assert(envi == envc);                                   \
        envv[envc] = NULL;                                      \
    }                                                           \
    va_end(va)

int execl(const char *path, const char *arg0, ...) {
    int exit;
    va_dup(arg0, 0);
    exit = execv(path, argv);
    free(argv);
    return exit;
}

/* Execute FILE, searching in the `PATH' environment variable if it contains no
   slashes, with all arguments after FILE until a NULL pointer and environment
   from `environ'.  */
int execlp(const char *file, const char *arg0, ...) {
    int exit;
    va_dup(arg0, 0);
    exit = execvp(file, argv);
    free(argv);
    return exit;
}

/* Execute PATH with all arguments after PATH until a NULL pointer, and the
   argument after that for environment.  */
int execle(const char *path, const char *arg0, ...) {
    int exit;
    va_dup(arg0, 1);
    exit = execve(path, argv, envv);
    free(argv);
    free(envv);
    return exit;
}

int execv(const char *path, char *const argv[]) {
    static int (*next)(const char *, char *const *);
    def_next(execv);

    NORM_CONFIG(path);

    RET_IF_DENY(norm, config);

    return next(path, argv);
}

int execvp(const char *file, char *const argv[]) {
    static int (*next)(const char *, char *const *);
    def_next(execvp);

    NORM_CONFIG(file);

    RET_IF_DENY(norm, config);

    return next(file, argv);
}

int execvpe(const char *file, char *const argv[], char *const envp[]) {
    static int (*next)(const char *, char *const *, char *const *);
    def_next(execvpe);

    NORM_CONFIG(file);

    RET_IF_DENY(norm, config);

    return next(file, argv, envp);
}

int execve(const char *file, char *const argv[], char *const envp[]) {
    static int (*next)(const char *, char *const *, char *const *);
    def_next(execve);

    NORM_CONFIG(file);

    RET_IF_DENY(norm, config);

    return next(file, argv, envp);
}
