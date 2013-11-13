#include <stdio.h>
#include <string.h>
#include "../qstr.h"

void main() {
    char *s = strdup("  hello, \"a b c\" or 'j k f',  My name is    Lenik.\n    ");
    char *tok;

    while ((tok = qstr_btok(&s)) != NULL) {
        printf("Token: %s.\n", tok);
    }
}
