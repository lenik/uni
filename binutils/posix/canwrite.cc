#include <stdio.h>

int main(int argc, char **argv) {
    FILE *f;

    argc--;
    argv++;
    if (argc < 1) {
        printf("canwrite PATH\n");
        return 2;
    }

    f = fopen(argv[0], "r+b");
    if (f == NULL)
        return 1;

    return 0;
}
