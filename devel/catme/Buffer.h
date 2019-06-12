#ifndef __FILE_H
#define __FILE_H

#include <sys/types.h>

typedef struct _Buffer {
    char *data;
    size_t size;
    size_t used;
} Buffer;

Buffer *Buffer_new(int capacity);

void Buffer_free(Buffer *buffer);

#define N_FREE_BUFFER(ref) \
    do { if (ref) { Buffer_free(ref); ref = NULL; } } while (0)

void Buffer_appendChar(Buffer *buffer, char ch);

void Buffer_appendStr(Buffer *buffer, const char *s);

char *Buffer_fgets(Buffer *buffer, File *fp);

#endif
