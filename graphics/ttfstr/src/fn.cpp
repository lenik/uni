#include "fn.h"

#include <stdio.h>
#include <stdlib.h>

#include <utility>
#include <bas/log.h>

using namespace std;

int utf8_next(const char** pcursor,
              const char* end) {
    const char* p = (const char*) *pcursor;
    int ch;

    if (p >= end)
        return -1;

    ch = (unsigned char) *p++;
    if (ch >= 0x80) {
        int  len;

        if (ch < 0xc0)                    /* malformed data */
            return -1;
        if (ch < 0xe0) {
            len = 1;
            ch &= 0x1f;
        } else if (ch < 0xf0) {
            len = 2;
            ch &= 0x0f;
        } else {
            len = 3;
            ch &= 0x07;
        }

        while (len-- > 0) {
            if (p >= end || (*p & 0xc0) != 0x80)
                return -1;                /* malformed data */
            ch = (ch << 6) | (*p++ & 0x3f);
        }
    }

    *pcursor = p;
    return ch;
}

pair<void *, size_t> fload(const char *path) {
    pair<void *, size_t> pair(0, 0);
    FILE *file = fopen(path, "rb");
    size_t size;
    void *buffer = NULL;

    if (file == NULL) {
        log_perr("Failed to open %s: ", path);
        return pair;
    }

    do {
        fseek(file, 0, SEEK_END);
        size = (size_t) ftell(file);
        fseek(file, 0, SEEK_SET);
        if (size <= 0) {
            log_err("Invalid file %s. ", path);
            break;
        }

        if (! (buffer = malloc(size))) {
            log_err("Memory out.");
            break;
        }

        if (fread(buffer, 1, size, file) != size) {
            log_perr("Failed to read file %s: ", path);
            free(buffer);
            break;
        }

        pair.first = buffer;
        pair.second = size;
    } while (0);

    fclose(file);
    return pair;
}
