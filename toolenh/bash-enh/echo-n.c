#include <stdio.h>

int main(int argc, char **argv) {

    int i;
    for (i = 1; i < argc; i++) {
        if (i != 1)
            putchar(' ');
        puts(argv[i]);
    }

    return 0;
}
