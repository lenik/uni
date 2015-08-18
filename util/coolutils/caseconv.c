#include "config.h"

#include <unistd.h>
#include <stdio.h>
#include <string.h>

#define UPPER   0                       /* ALL UPPER CASE */
#define LOWER   1                       /* ALL LOWER CASE */
#define CAPTION 2                       /* In Caption Case */
#define DASH    3                       /* in dash-line */
#define CAMEL   4                       /* in mixedCase */
#define MECH    5                       /* In CaptionCase */

#define VOID    EOF

char *program;
int opt_echo = 0;
int opt_case = UPPER;
int opt_dash = '-';

const char *case_help[] = {
    "UPPER CASE",
    "lower case",
    "Caption Case",
    "dash-line case",
    "camelCase",
    "MechnicalCase",
};

typedef int (*TRFUNC)(int c);

int tr_upper(int c) {
    return toupper(c);
}

int tr_lower(int c) {
    return tolower(c);
}

int tr_caption(int c) {
    static int word_off = 0;
    if (isalpha(c)) {
        if (word_off)
            c = tolower(c);
        else
            c = toupper(c);
        word_off++;
    } else
        word_off = 0;
    return c;
}

int tr_dash(int c) {
    static int word_off = 0;
    if (isalpha(c)) {
        if (word_off)
            if (isupper(c)) {
                putchar(opt_dash);
                c = tolower(c);
            }
        word_off++;
    } else
        word_off = 0;
    return c;
}

int tr_camel(int c) {
    static int last = 0;
    int c0 = c;
    if (c == opt_dash)                  /* [-] */
        c = VOID;
    else if (last == opt_dash) {         /* -c */
        // printf("last=%c, c=%c\n", last, c);
        if (isalpha(c))
            c = toupper(c);             /* _alpha => Alpha */
        else
            putchar(opt_dash);          /* restore [-] */
    }
    last = c0;
    return c;
}

/* TODO - not implemented. */
int tr_mech(int c) {
    static int word_off = 0;
    // static char last = 0;
    if (isalpha(c)) {
        if (word_off)
            c = tolower(c);
        else
            c = toupper(c);
        word_off++;
    } else
        word_off = 0;
    return c;
}

void show_version() {
    printf("%s: translate text to %s\n", program, case_help[opt_case]);
    printf("Version 0.1,  Written by Lenik,  Aug 2010\n");
}

void show_help() {
    show_version();
    printf("\n");
    printf("Syntax: \n");
    printf("    -e, --echo       translate the arguments itself\n");
    printf("    -u, --upper      to UPPER CASE\n");
    printf("    -l, --lower      to lower case \n");
    printf("    -c, --caption    to Caption Case\n");
    printf("    -d, --dash       to dash-line case\n");
    printf("    -j, --camel      to camelCase\n");
    printf("    -m, --mech       to CaptionCase case\n");
    printf("    -h, --help       show this help page\n");
    printf("        --version    show version info\n");
}

int main(int argc, char **argv) {

    int c;
    int i;
    TRFUNC trf = NULL;

    program = argv[0];
    --argc; ++argv;

    char *program_base = strrchr(program, '/');
    if (! program_base)
        program_base = program;

    if (! strcmp(program_base, "uc"))
        opt_case = UPPER;
    else if (! strcmp(program_base, "lc"))
        opt_case = LOWER;
    else if (! strcmp(program_base, "to-upper"))
        opt_case = UPPER;
    else if (! strcmp(program_base, "to-lower"))
        opt_case = LOWER;
    else if (! strcmp(program_base, "to-caption"))
        opt_case = CAPTION;
    else if (! strcmp(program_base, "to-dash")) /* see: bsdmainutils:ul */
        opt_case = DASH;
    else if (! strcmp(program_base, "to-camel"))
        opt_case = CAMEL;
    else if (! strcmp(program_base, "to-mech"))
        opt_case = MECH;

    while (argc) {
        const char *arg = *argv;
        if (*arg == '-') {
            // printf("opt: %s\n", arg);
            argc--;
            argv++;
            arg++;
            if (*arg == '-') {          /* --? */
                arg++;
                if (! *arg)             /* -- */
                    break;
                if (! strcmp("echo", arg))
                    opt_echo = 1;
                else if (! strcmp("upper", arg))
                    opt_case = UPPER;
                else if (! strcmp("lower", arg))
                    opt_case = LOWER;
                else if (! strcmp("caption", arg))
                    opt_case = CAPTION;
                else if (! strcmp("dash", arg))
                    opt_case = DASH;
                else if (! strcmp("camel", arg))
                    opt_case = CAMEL;
                else if (! strcmp("mech", arg))
                    opt_case = MECH;
                else if (! strcmp("help", arg)) {
                    show_help();
                    return 0;
                }
                else if (! strcmp("version", arg)) {
                    show_version();
                    return 0;
                }
                else {
                    fprintf(stderr, "Invalid option: %s", arg);
                    return 1;
                }
            } // --
            else {
                while (c = *arg++) {
                    switch (c) {
                    case 'e':
                        opt_echo = 1; break;
                    case 'u':
                        opt_case = UPPER; break;
                    case 'l':
                        opt_case = LOWER; break;
                    case 'c':
                        opt_case = CAPTION; break;
                    case 'd':
                        opt_case = DASH; break;
                    case 'j':
                        opt_case = CAMEL; break;
                    case 'm':
                        opt_case = MECH; break;
                    case 'h':
                        show_help();
                        return 0;
                        break;
                    default:
                        fprintf(stderr, "Invalid option: %c\n", c);
                        return 1;
                    }
                }
            } // -x
        } // -
        else
            break;
    } // while argc

    switch (opt_case) {
    case UPPER:
        trf = tr_upper; break;
    case LOWER:
        trf = tr_lower; break;
    case CAPTION:
        trf = tr_caption; break;
    case DASH:
        trf = tr_dash; break;
    case CAMEL:
        trf = tr_camel; break;
    case MECH:
        trf = tr_mech; break;
    }

    if (opt_echo) {
        for (i = 0; i < argc; i++) {
            const char *s = *argv++;
            // printf("arg: %s\n", s);
            if (i) putchar(trf(' '));
            while (c = *s++) {
                c = trf(c);
                if (c != VOID)
                    putchar(c);
            }
        }
        printf("\n");
        return 0;
    }

    /* no filename or '-' is specified */
    if (argc == 0 ||
        (argc == 1 && ! strcmp(argv[0], "-"))) {
        while ((c = getchar()) != EOF) {
            c = trf(c);
            if (c != VOID)
                putchar(c);
        }
        return 0;
    }

    int file_index;
    for (file_index = 0; i < argc; i++) {
        char *filename = argv[file_index];
        // printf("file: %s\n", filename);
        FILE *f = fopen(filename, "rb");
        if (!f) {
            fprintf(stderr, "Failed to open file %s: ", filename);
            perror("");
            return 1;
        }
        while ((c = fgetc(f)) != EOF) {
            c = trf(c);
            if (c != VOID)
                putchar(c);
        }
        fclose(f);
    }
    return 0;
}
