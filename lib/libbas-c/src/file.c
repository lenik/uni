#include <assert.h>
#include <stdio.h>
#include <stdlib.h>                     /* malloc, realloc, free */
#include <string.h>                     /* memcpy */
#include <bas/file.h>

#define BLOCK_SIZE 64

char *load_file(FILE *in, const char *path, size_t *sizep,
                size_t mem_off, size_t padding) {
    size_t capacity = 16;
    char *data = (char *) malloc(capacity);
    size_t off = mem_off;
    char buf[BLOCK_SIZE];
    size_t cb;
    int err = 0;

    while (! feof(in)) {
        cb = fread(buf, 1, BLOCK_SIZE, in);
        if (ferror(in)) {
            error("Failed to read from file %s: ", path);
            perror("");
            err = 1;
            break;
        }

        size_t remaining;
        while (1) {
            remaining = capacity - off - padding;
            if (remaining >= cb)
                break;

            data = realloc(data, capacity * 2);
            if (data == NULL) {
                error("Insufficient memory to load file %s.\n", path);
                exit(1);
            }
            capacity *= 2;
        }

        memcpy(data + off, buf, cb);
        off += cb;
    }

    if (err) {
        if (data != NULL) {
            free(data);
            data = NULL;
        }
    } else {
        if (sizep)
            *sizep = off - mem_off;
    }
    return data;
}
