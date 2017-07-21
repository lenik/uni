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

typedef enum _FileLang {
    FileLang_C,
    FileLang_SQL,
    FileLang_UNIX,
    FileLang_XML,
} FileLang;

FileLang Lang_parse(const char *s);
const char *Lang_toString(FileLang lang);

typedef struct _Frame {
    char *del;                          /* left delimitor */
    char *der;                          /* right delimitor */
    char *idel;                         /* inline left delimitor */
    char *ider;                         /* inline right delimitor */

    char *ext;                          /* this file extension */
    char *dir;                          /* this directory */
} Frame;

Frame *Frame_new();
void Frame_free(Frame *frame);
void Frame_initExt(Frame *frame, const char *ext);

Frame *Stack_enter();
void Stack_leave();
Frame *Stack_peek();

#endif
