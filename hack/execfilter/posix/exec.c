#include "posix_fn.h"

#define PTRSIZE sizeof(void *)

#define va_ptrsz(vp, arg0_ref, countp) \
    _va_ptrsz(&(vp), (void **) (arg0_ref), countp)

static char **_va_ptrsz(va_list *vp_ref, void **arg0_ref, int *countp) {
    va_list vq;
    va_copy(vq, *vp_ref);

    int n = 0;
    if (arg0_ref) {
        if (*arg0_ref) {
            n++;
            while (va_arg(*vp_ref, char *) != NULL)
                n++;
        }
    } else {
        while (va_arg(*vp_ref, char *) != NULL)
            n++;
    }

    char **ptrs = (char **) malloc((n + 1) * PTRSIZE);
    char *ptr;
    int i = 0;

    if (arg0_ref) {
        if (*arg0_ref) {
            ptrs[i++] = (char *) *arg0_ref;
            while ((ptr = va_arg(vq, char *)) != NULL)
                ptrs[i++] = ptr;
        }
    } else {
        while ((ptr = va_arg(vq, char *)) != NULL)
            ptrs[i++] = ptr;
    }

    assert(i == n);
    ptrs[n] = NULL;

    if (countp)
        *countp = n;

    return ptrs;
}

/* p for PATH-search, e for environ:
   execl, execlp, execle, execlpe, execv, execvp, execvpe, execve */

int execl(const char *path, const char *arg0, ...) {
    va_list vp;
    va_start(vp, arg0);
    char **argv = va_ptrsz(vp, &arg0, NULL);
    va_end(vp);

    int exit;
    exit = execv(path, argv);
    free(argv);
    return exit;
}

/* Execute FILE, searching in the `PATH' environment variable if it contains no
   slashes, with all arguments after FILE until a NULL pointer and environment
   from `environ'.  */
int execlp(const char *file, const char *arg0, ...) {
    va_list vp;
    va_start(vp, arg0);
    char **argv = va_ptrsz(vp, &arg0, NULL);
    va_end(vp);

    int exit;
    exit = execvp(file, argv);
    free(argv);
    return exit;
}

/* Execute PATH with all arguments after PATH until a NULL pointer, and the
   argument after that for environment.  */
int execle(const char *path, const char *arg0, ...) {
    // int argc, envc, i;

    va_list vp;
    va_start(vp, arg0);
    // char **argv = va_ptrsz(vp, &arg0, &argc);
    // char **envv = va_ptrsz(vp, NULL, &envc);
    char **argv = va_ptrsz(vp, &arg0, NULL);
    char **envv = va_ptrsz(vp, NULL, NULL);
    va_end(vp);

    // for (i = 0; i < argc; i++)
    // printf("arg %d: %s\n", i, argv[i]);
    // for (i = 0; i < envc; i++)
    // printf("env %d: %s\n", i, envv[i]);

    int exit;
    exit = execve(path, argv, envv);
    free(argv);
    free(envv);
    return exit;
}

int execv(const char *path, char *const argv[]) {
    static int (*next)(const char *, char *const *);
    def_next(execv);

    NORM_CONFIG(path);

    RET_IF_DENY(norm, mode);

    return next(path, argv);
}

int execvp(const char *file, char *const argv[]) {
    static int (*next)(const char *, char *const *);
    def_next(execvp);

    NORM_CONFIG(file);

    RET_IF_DENY(norm, mode);

    return next(file, argv);
}

int execvpe(const char *file, char *const argv[], char *const envp[]) {
    static int (*next)(const char *, char *const *, char *const *);
    def_next(execvpe);

    NORM_CONFIG(file);

    RET_IF_DENY(norm, mode);

    return next(file, argv, envp);
}

int execve(const char *file, char *const argv[], char *const envp[]) {
    static int (*next)(const char *, char *const *, char *const *);
    def_next(execve);

    NORM_CONFIG(file);

    RET_IF_DENY(norm, mode);

    return next(file, argv, envp);
}
