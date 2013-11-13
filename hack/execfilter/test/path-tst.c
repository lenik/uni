#include <stdio.h>
#include <string.h>
#include "../path.h"

void main() {
    char *progs[] = {
        "df", "hd", "less", "what", "sh", "ever..."
    };
    int n = sizeof(progs) / sizeof(char *);
    int i;

    for (i = 0; i < n; i++) {
        char *prog = progs[i];
        char *which = path_find(prog);
        printf("%s => %s.\n", prog, which);
    }
}
