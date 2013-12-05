#include <sys/stat.h>
#include <assert.h>
#include <limits.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <bas/path.h>

char *path_normalize(const char *path) {
    char real[PATH_MAX];
    if (realpath(path, real) == NULL)
        return NULL;
    else
        return strdup(real);
}

static char *pathv;

/* the result should be free-ed if it's non-null. */
char *path_find(const char *name) {
    if (! pathv) {
        char *pathenv = getenv("PATH");
        if (pathenv == NULL)
            pathenv = "";
        int pathenvlen = strlen(pathenv);

        pathv = malloc(pathenvlen + 2);
        strcpy(pathv, pathenv);
        pathv[pathenvlen + 1] = '\0';

        char *p = pathv;
        while (*p) {
            if (*p == ':')
                *p = '\0';
            p++;
        }
    }

    char *p = pathv;
    char join[PATH_MAX];
    struct stat sb;

    while (*p) {
        strcpy(join, p);
        strcat(join, "/");
        strcat(join, name);

        if (stat(join, &sb) >= 0) {     /* file exists. */
            mode_t mode = sb.st_mode;
            int xbits = mode & (S_IXUSR | S_IXGRP | S_IXOTH);
            if (xbits != 0)
                return strdup(join);
        }

        p += strlen(p) + 1;
    }

    return NULL;
}

char *path_find_norm(const char *name) {
    assert(name != NULL);

    char *norm;

    if (*name != '/') {
        char *path_expansion = path_find(name);
        if (path_expansion != NULL) {
            norm = path_normalize(path_expansion);
            free(path_expansion);
            return norm;
        }
    }

    norm = path_normalize(name);
    return norm;
}
