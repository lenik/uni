#include <memory.h>
#include "buffer.h"

Buffer *Buffer_new(int capacity) {
    Buffer *buffer = (Buffer *) malloc(sizeof(Buffer));
    buffer->data = malloc(capacity);
    buffer->size = capacity;
    buffer->used = 0;
    return buffer;
}

void Buffer_free(Buffer *buffer) {
    free(buffer->data);
    free(buffer);
}

void Buffer_appendChar(Buffer *buffer, char ch) {
}

void Buffer_appendStr(Buffer *buffer, const char *s) {
}

char *Buffer_fgets(Buffer *buffer, File *fp) {
    if (feof(fp))
        return NULL;
    return buffer->data;
}
