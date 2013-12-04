#include <sys/stat.h>
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

char *path_find(const char *name) {
    if (! pathv) {
        char *paths = getenv("PATH");
        pathv = strdup(paths);
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
    char *which = path_find(name);
    if (which) {
        char *norm = path_normalize(which);
        // free(which);
        return norm;
    } else {
        char *norm = path_normalize(name);
        return norm;
    }
}
