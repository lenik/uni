#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../qstr.h"

void main() {
    char *s = strdup("  hello, \"a b c\" or 'j k f',  My name is    Lenik.\n    ");
    char *p = s;
    char *tok;

    while ((tok = qstr_btok(p, &p)) != NULL) {
        printf("Token: %s.\n", tok);
    }

    free(s);
}
