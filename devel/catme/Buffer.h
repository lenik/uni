#ifndef __FILE_H
#define __FILE_H

#include <sys/types.h>

typedef struct _Buffer {
    char *data;
    size_t size;
    size_t used;
} Buffer;

void Buffer_init(Buffer *buffer, int capacity);
void Buffer_append(Buffer *buffer, char ch);
void Buffer_appendStr(Buffer *buffer, const char *s);

#endif
