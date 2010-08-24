#include <stdio.h>
#include <stdlib.h>

long gcd(long a, long b) {
    if (a == 0)
        return b;
    if (b == 0)
        return a;
    if (a >= b)
        return gcd(a % b, b);
    else
        return gcd(a, b % a);
}

int main(int argc, char **argv) {

    long g = 1l;
    long arg;

    --argc;
    ++argv;

    if (argc == 0) {
        printf("syntax: gcd INTEGERS...");
        return 1;
    }

    argc--;
    arg = strtol(*argv++, NULL, 0);
    if (arg != 0)
        g = arg > 0 ? arg : -arg;

    while (argc--) {
        arg = strtol(*argv++, NULL, 0);
        if (arg == 0)
            continue;
        g = gcd(g, arg);
    }

    printf("%ld\n", g);
    return 0;
}
