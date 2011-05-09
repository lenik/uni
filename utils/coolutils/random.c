#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

int seed = 0;

char *rdev = "/dev/random";             /* Set to NULL to using stdlib random */
int size = 4;
int radix = 10;
long limit = 0;

const char *tab = "0123456789abcdefghijklmnopqrstuvwxyz";

#define BITS (sizeof(int) * 8)

unsigned hash(const void *mem, size_t size) {
    const unsigned char *p = (const unsigned char *) mem;
    unsigned h = 0;
    while (size--) {
        h = (h << 8) | (h >> (BITS - 8));
        h ^= *p++;
    }
    return h;
}

int main(int argc, char **argv) {

    int c;
    const char *param_name = NULL;      /* param name for error report */
    int *int_param = NULL;              /* non-null if want an int param */
    long *long_param = NULL;

    int max_radix = strlen(tab);

    --argc;
    ++argv;

    while (argc) {
        char *arg = *argv++;
        argc--;

        /* Check option before param, only positive int is allowed. */
        if (*arg == '-') {
            param_name = NULL;
            while (c = *++arg) {
                switch (c) {
                case 'u':
                    rdev = "/dev/urandom";
                    break;
                case 'r':
                    if (arg[1]) {
                        radix = (int) strtol(++arg, NULL, 10);
                        continue;
                    } else {
                        param_name = "radix";
                        int_param = &radix;
                    }
                    break;
                case 'w':
                    if (arg[1]) {
                        size = (int) strtol(++arg, NULL, 10);
                        continue;
                    } else {
                        param_name = "width";
                        int_param = &size;
                    }
                    break;
                case 'c':
                    rdev = NULL;
                    if (arg[1]) {
                        seed = (int) strtol(++arg, NULL, 10);
                        continue;
                    } else {
                        param_name = "seed",
                        int_param = &seed;
                    }
                    break;
                case 'l':
                    if (arg[1]) {
                        limit = strtol(++arg, NULL, 10);
                        continue;
                    } else {
                        param_name = "limit";
                        long_param = &limit;
                    }
                    break;
                default:
                    fprintf(stderr, "Invalid option: %c\n", c);
                    return 1;
                }
            }
            continue;
        } /* -? */

        if (param_name) {
            char *endptr = arg;

            if (int_param)
                *int_param = (int) strtol(arg, &endptr, 0);
            else if (long_param)
                *long_param = strtol(arg, &endptr, 0);

            if (*endptr) {
                fprintf(stderr, "Invalid value for parameter %s: %s\n",
                        param_name, arg);
                return 2;
            }
            param_name = NULL;
            continue;
        }

        /* other argument */
        fprintf(stderr, "Unexpected argument: %s\n", arg);
        return 1;
    }

    if (radix == 0) {
        fprintf(stderr, "Radix is 0");
        return 1;
    }
    if (radix > max_radix) {
        fprintf(stderr, "Radix is too large: %d\n", radix);
        return 1;
    }

    if (rdev) {
        FILE *f = fopen(rdev, "rb");
        if (f == NULL) {
            perror("Can't open random file: ");
            return 3;
        }
        if (seed) {
            fprintf(stderr, "Warning: seed is ignored\n");
        }
        while (size--) {
            c = fgetc(f);
            if (c == EOF)
                c = 0xcd;               /* PAD with 0CDh*/

        }
        fclose(f);
    } else {
        char buf[100];
        int len = 0;

        if (seed)
            srandom((unsigned) seed);
        else {
            struct tm tm;
            timegm(&tm);
            seed = hash(&tm, sizeof(tm));
            srandom((unsigned) seed);
        }

        long rand = random();
        if (limit)
            rand %= limit;

        if (rand == 0)
            putchar('0');
        else {
            while (rand) {
                int digit = rand % radix;
                rand -= digit;
                rand /= radix;
                buf[len++] = tab[digit];
            }
            while (--len)
                putchar(buf[len]);
        }
    }
    putchar('\n');
    return 0;
}
