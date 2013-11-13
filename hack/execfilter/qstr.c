#include <ctype.h>
#include <string.h>

#include "qstr.h"

char *qstr_btok(char **headp) {
    char *tok = *headp;
    char *endp;
    char delim = 0;                     /* delim by space or NUL. */

    /* trim left. */
    while (isspace(*tok)) tok++;

    switch (*tok) {
    case 0:
        return NULL;
    case '"':
        delim = '"';
        tok++;
        break;
    case '\'':
        delim = '\'';
        tok++;
        break;
    }

    endp = tok;
    while (*endp) {
        if (delim == 0) {
            if (isspace(*endp))
                break;
        } else {
            if (*endp == delim)
                break;
        }
        endp++;
    }

    *endp++ = '\0';
    *headp = endp;
    return tok;
}
