#include <ctype.h>
#include <string.h>

#include "qstr.h"

char *qstr_btok(char *head, char **endp) {
    char *tok = head;
    char *end;
    char delim = 0;                     /* delim by space or NUL. */
    char ch;

    /* trim left. */
    while (isspace(*tok)) tok++;

    switch (*tok) {
    case 0:
        return NULL;                    /* EOT or trailing space only */
    case '"':
        delim = '"';
        tok++;
        break;
    case '\'':
        delim = '\'';
        tok++;
        break;
    }

    end = tok;
    if (delim == 0) {
        while ((ch = *end)) {
            if (isspace(ch))
                break;
            end++;
        }
    } else {
        while ((ch = *end++)) {
            if (ch == delim)
                break;
        }
    }

    if (*end)
        *end++ = '\0';
    if (endp)
        *endp = end;
    return tok;
}
