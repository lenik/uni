#include <stdio.h>
#include <stdlib.h>

#include <windows.h>

#define CMDW

void help();

#ifndef CMDW

void check(BOOL pred, const char *mesg);
int main(int argc, char **argv) {

#else

#include <shellapi.h>
#include <atlconv.h>
int _stdcall WinMain(HINSTANCE hInst,
                     HINSTANCE hPrevInst,
                     LPSTR cmdLine,
                     int nCmdShow) {
    USES_CONVERSION;
    int _argc;
    LPWSTR cmdLineW = A2W(cmdLine);
    LPWSTR *argvW = CommandLineToArgvW(cmdLineW, &_argc);
    int argc = _argc + 1;
    char **argv = (char **) malloc(argc * sizeof(char *));
    argv[0] = "winevent";
    for (int i = 0; i < _argc; i++) {
        argv[i + 1] = W2A(argvW[i]);
        // printf("%d. %s\n", i, argv[i + 1]);
    }
#endif

    argc--;
    argv++;
    L: while (argc >= 0) {
        char *arg = *argv;
        if (*arg == '-') {
            switch (*++arg) {
            case 'x':                   /* name */
                check(--argc, "-x EXTNAME");
                opt_ext = _strdup(*++argv);
                break;
            case 'f':                   /* wait */
                opt_force = 1;
                break;
            default:
                break L;
            }
        }
        argc--;
        argv++;
    }
    check(argc >= 2, "SOURCE and DEST must be specified. ");

    printf("src=%s\n", argv[0]);
    printf("dst=%s\n", argv[1]);
    return 0;
}

void check(BOOL pred, const char *mesg) {
    if (pred)
        return;
    fprintf(stderr, "error: %s\n", mesg);
    exit(1);
}

void help() {
    printf("lnk [OPTIONS] SOURCE DEST\n"
           "\n"
           "Options: \n"
           "  -x EXTNAME       default .lnk\n"
           "  -f               overwrite the existing\n"
           );
}
