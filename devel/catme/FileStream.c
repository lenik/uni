#include "file.h"

#include <stdio.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

bool FileStream_readLine(File *f, GString *buf, bool chomp) {
    int ch;
    int len = 0;
    int saved = -1;
    while ((ch = fgetc(f)) != EOF) {
        len++;

        if (ch == '\r') {
            saved = ch;
            continue;
        }
        if (ch == '\n')
            if (chomp) break;

        if (saved != -1) {
            g_string_append_c(buf, saved);
            saved = -1;
        }

        g_string_append_c(buf, ch);

        if (ch == '\n')
            break;
    }
    return len != 0;
}
