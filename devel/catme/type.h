#ifndef __TYPE_H
#define __TYPE_H

#include <glib.h>

#define FREE(ptr) \
    do { if (ptr) { free(ptr); ptr = NULL; } } while (0)

#define SET_STRDUP(ptr, val)     \
    do { if (ptr) { free(ptr); } \
        if (val) ptr = strdup(val); else ptr = NULL; \
    } while (0)

extern GList *pathv;
extern GQueue *stack;

#endif
