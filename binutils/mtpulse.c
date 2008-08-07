
/**
 * Multi-Thread `Pulse' Output Generator
 *
 * @example
 *    mtpulse begin 1s hello 0.5s X a b c ?10
 */

#include <stdio.h>
#include <stdlib.h>

#include <windows.h>
/* void Sleep(unsigned int dwMilliseconds); */

FILE *in;
FILE *out;
int newline = 1;

void output(FILE *f, const char *msg) {
    if (newline) {
        fprintf(f, "%s\n", msg);
    } else {
        fputs(msg, f);
    }
    fflush(f);
    Sleep(100); /* safe interval for thread-switch */
}

int main(int argc, char **argv) {
    int retval = 0;
    char buf[1000];
    in = stdin;
    out = stdout;
    argc--;
    argv++;
    while (argc--) {
        char *arg = *argv++;
        if (*arg >= '0' && *arg <= '9') {
            char *end;
            double n = strtod(arg, &end);
            switch (*end) {
            case 'u': /* micro sec */
                n /= 1000;
                break;
            case 'm': /* milli sec */
                break;
            case 's': /* sec */
                n *= 1000;
                break;
            case 'M': /* minute */
                n *= 1000 * 60;
                break;
            }
            Sleep(n);
            continue;
        }
        switch (*arg++) {
        case 'H':
            printf(
                "mtpulse (CONTROL|STRING|SLEEP)*\n"
                "\n"
                "CONTROL: \n"
                "   H show this help\n"
                "   ?NUM set return value\n"
                "   N newline after each string\n"
                "   C continue without newline\n"
                "   X output to stderr\n"
                "   O output to stdout\n"
                "   I read input\n"
                "   E echo input\n"
                );
            break;
        case '?':
            retval = atoi(arg);
            break;
        case 'N':
            newline = 1;
            break;
        case 'C':
            newline = 0;
            break;
        case 'X':
            if (*arg)
                output(stderr, arg);
            else
                out = stderr;
            break;
        case 'O':
            if (*arg)
                output(stdout, arg);
            else
                out = stdout;
            break;
        case 'I':
            fgets(buf, sizeof(buf), in);
            break;
        case 'E':
            fgets(buf, sizeof(buf), in);
            output(out, buf);
            break;
        default:
            output(out, arg - 1);
            break;
        }
    }
    return retval;
}
