#include <memory.h>
#include "buffer.h"

void Buffer_init(Buffer *buffer, int capacity) {
    buffer->data = malloc(capacity);
    buffer->size = capacity;
    buffer->used = 0;
}

void Buffer_append(Buffer *buffer, char ch) {
}

void Buffer_appendStr(Buffer *buffer, const char *s) {
}
