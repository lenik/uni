#include <stdio.h>
#include <unistd.h>

void main(int argc, char **argv) {
    int i;
    char *file;

    argv++; argc--;
    file = argv[0];
    for (i = 0; i < argc; i++) {
        if (i > 0)
            putchar(' ');
        printf("%s", argv[i]);
    }
    putchar('\n');
    execvp(file, argv);
}
