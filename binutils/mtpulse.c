
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

int main(int argc, char **argv) {
    int retval = 0;
    char buf[1000];
    int newline = 1;
    FILE *in = stdin;
    FILE *out = stdout;
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
        switch (*arg) {
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
                "   E echo input\n"
                );
            break;
        case '?':
            retval = atoi(arg + 1);
            break;
        case 'X':
            out = stderr;
            break;
        case 'O':
            out = stdout;
            break;
        case 'N':
            newline = 1;
            break;
        case 'C':
            newline = 0;
            break;
        case 'E':
            fgets(buf, sizeof(buf), in);
            fputs(buf, out);
            if (newline)
                fputs("\n", out);
            break;
        case 'R':
            fgets(buf, sizeof(buf), in);
            break;
        default:
            fputs(arg, out);
            if (newline)
                fputs("\n", out);
            break;
        }
    }
    return retval;
}
