#include <stdio.h>

#define LINE_MAX    4096

#define INT     0
#define LONG    1
#define FLOAT   2
#define DOUBLE  3

#define SUM     '+'
#define OR      '|'
#define AND     '&'
#define AVERAGE 'a'
#define MID     'M'

int valtype = INT;
int alg = SUM;

int     index;
int     iresult;
long    lresult;
float   fresult;
double  dresult;

#define DROP(type, result) \
    void drop_##type(type n) { \
        switch (alg) { \
        case SUM:   result += n; \
        case OR:    result |= n; \
        case AND:   result &= n; \
        case MEAN:  result += n; \
        case MID:   ; \
        } \
    }
DROP(int, iresult)
DROP(long, iresult)
DROP(float, iresult)
DROP(double, iresult)

char linebuf[LINE_MAX];

void process(FILE *file) {
    long lval;
    double dval;

    while (fgets(linebuf, LINE_MAX, file) != NULL) {
        switch (valtype) {
        case INT:
            lval = strtol(linebuf, 0, NULL);
            if (index == 0)
                iresult = (int) lval;
            else
                drop_int((int) lval);
            break;

        case LONG:
            lval = strtol(linebuf, 0, NULL);
            if (index == 0)
                lresult = lval;
            else
                drop_long(lval);
            break;

        case FLOAT:
            dval = strtol(linebuf, 0, NULL);
            if (index == 0)
                fresult = (float) dval;
            else
                drop_float((float) dval);
            break;

        case DOUBLE:
            dval = strtol(linebuf, 0, NULL);
            if (index == 0)
                dresult = dval;
            else
                drop_double((double) dval);
            break;
        } // switch valtype
        index++;
    } // while fgets
}

int main(int argc, char **argv) {
    char opt;

    argc--;
    argv++;

    while (argc > 0) {
        char *arg = *argv;
        switch (arg[0]) {
        case '-':
            while (opt = *++arg) {
                switch (opt) {
                case 's':
                    alg = SUM; break;
                case 'a':
                    alg = AVERAGE; break;
                case 'O':
                    alg = OR; break;
                case 'A':
                    alg = AND; break;
                case 'M':
                    alg = MID; break;
                case 'h':
                    printf("sum [-saOAMh] FILES\n");
                    return 0;
                default:
                    printf("Bad option: %c\n", opt);
                    return 1;
                }
            }
            argv++;
            break;
        }
    }

    return 0;
}
