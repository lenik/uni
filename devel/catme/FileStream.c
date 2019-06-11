#include "file.h"

#include <stdio.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

bool FileStream_readLine(File *f, Buffer *buf) {
    int ch;
    size_t size = 0;
    buf->used = 0;
    while ((ch = fgetc(f)) != EOF) {
        size++;
        if (buf->used == buf->size) {
            size_t newSize = buf->size * 2;
            char *p;
            if (newSize == 0) {
                newSize = 4;
                p = malloc(newSize);
            } else {
                p = realloc(buf->data, newSize);
            }
            if (p == NULL) {
                perror("Memory out");
                exit(1);
            }
            buf->data = p;
            buf->size = newSize;
        }
        buf->data[buf->used++] = ch;
    }
    return size != 0;
}
