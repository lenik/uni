/**
 * If the event is existed, then wait for it
 * If the event isn't existed, then create and wait it be trigged
 *
 * @example
 *    winevent -t 1000 -n myEvent1
 */

#include <stdio.h>
#include <stdlib.h>

#include <windows.h>

#undef CMDW

void check(BOOL pred, const char *mesg);
void help();

LPCTSTR opt_param = NULL;

DWORD opt_timeout = 10000;

#ifndef CMDW

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
    while (argc--) {
        char *arg = *argv++;
        if (*arg == '-') {
            switch (*++arg) {
            case 'p':                   /* name */
                check(argc--, "-p PARAMETER");
                opt_param = _strdup(*argv++);
                break;
            case 't':                   /* timeout */
                check(argc--, "-t TIMEOUT");
                opt_timeout = (DWORD) strtol(*argv++, NULL, 0);
                break;
            case 'h':                   /* help */
                help();
                return 0;
            default:
                printf("unknown option -%s\n", arg);
                return 1;
            }
        } else if (opt_param == NULL)
            opt_param = _strdup(arg);
        else {
            printf("unknown option %s\n", arg);
            return 1;
        }
    }

    check(opt_param != NULL, "PARAMETER isn't specified. ");

    DWORD result;
    //printf("bcast to %x with param=%s timeout=%d\n",
    //    HWND_BROADCAST, opt_param, opt_timeout);
    result = SendMessageTimeout(HWND_BROADCAST,
                            WM_SETTINGCHANGE, /* opt */
                            NULL,
                            (LPARAM) opt_param,
                            SMTO_BLOCK,
                            opt_timeout,
                            &result);
    if (result == 0) {
        DWORD err = GetLastError();
        printf("Failed: %d\n", err);
        if (err == ERROR_TIMEOUT)
            printf("Timeout\n");
        return err;
    }
    return 0;
}

void check(BOOL pred, const char *mesg) {
    if (pred)
        return;
    fprintf(stderr, "error: %s\n", mesg);
    exit(1);
}

void help() {
    printf("wbcast OPTIONS [PARAMETER]\n"
           "\n"
           "Options: \n"
           "  -p PARAMETER     parameter name to broadcast\n"
           "  -t TIMEOUT       sendmessage timeout\n"
           );
}
