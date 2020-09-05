#ifndef __Nullable_H
#define __Nullable_H

#include <stdlib.h>
#include <string.h>

#define N_FREE(ref) \
    do { if (ref) { free(ref); ref = NULL; } } while (0)

#define N_SET(ref, val) \
    do { if (ref) { free(ref); } \
        ref = val; \
    } while (0)

char *g_strdup(const char *s);
size_t N_strlen(const char *s);

#endif
