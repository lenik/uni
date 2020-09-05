#include <sys/ctype.h>
#include <string.h>

#include "Chars.h"

char *Chars_replace(char *s, char from, char to) {
    char *p = s;
    char ch;
    while (ch = *p) {
        if (ch == from)
            *p = to;
        p++;
    }
    return s;
}

const char *Chars_trimLeft(const char *s) {
    if (s == NULL)
        return NULL;
    while (*s && isspace(*s))
        s++; // trim-left
    return s;
}

const char *Chars_trimRight(const char *s) {
    int len = strlen(s);
    while (len && isspace(s[len - 1]))
        len--;
    const char *end = s + len;
    *end = 0;
    return end;
}
