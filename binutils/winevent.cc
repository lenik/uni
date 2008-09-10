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

void check(BOOL pred, const char *mesg);
void help();

const char *opt_name = NULL;

/*
 * w: wait mode, create the event and waiting for signal
 *
 * m: wait mode, wait/manual-reset loop
 *
 * r: reset the event
 *
 * s: signal/set the event
 */
char opt_mode = 'w';
BOOL opt_exclusive = FALSE;
DWORD opt_timeout = INFINITE;

HANDLE hEvent;

#ifdef CONSOLE

int main(int argc, char **argv) {

#else

#include <shellapi.h>
#include <atl.h>
int _stdcall WinMain(HINSTANCE hInst,
                     HINSTANCE hPrevInst,
                     int nCmdShow,
                     LPCSTR cmdLine) {
    USES_CONVERSION;
    int argc;
    LPWSTR *argvW = CommandLineToArgvW(cmdLineW, &argc);
    argc++;
    char **argv = (char **) malloc(argc * sizeof(char *));
    argv[0] = "winevent";
    for (int i = 1; i < argc; i++) {
        argv[i] = W2A(argvW[i - 1]);
        printf("%d. %s\n", i, argv[i]);
    }
#endif

    char buf[1000];
    DWORD ret;
    argc--;
    argv++;
    while (argc--) {
        char *arg = *argv++;
        if (*arg == '-') {
            switch (*++arg) {
            case 'n':                   /* name */
                check(argc--, "-n EVENTNAME");
                opt_name = _strdup(*argv++);
                break;
            case 'w':                   /* wait */
                opt_mode = 'w';
                break;
            case 'm':                   /* manual-reset wait */
                opt_mode = 'm';
                check(0, "manual-reset isn't unsupported yet.");
                break;
            case 'x':
                opt_exclusive = TRUE;
                break;
            case 'r':                   /* reset */
                opt_mode = 'r';
                break;
            case 's':                   /* signal */
                opt_mode = 's';
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
        } else if (opt_name == NULL)
            opt_name = _strdup(arg);
        else {
            printf("unknown option %s\n", arg);
            return 1;
        }
    }

    check(opt_name != NULL, "EVENTNAME isn't specified. ");
    switch (opt_mode) {
    case 'w':                           /* wait/reset loop */
        hEvent = CreateEvent(NULL,      /* event attributes */
                             FALSE,     /* manual reset */
                             FALSE,     /* initial state */
                             opt_name);
        check(hEvent != NULL, "failed to CreateEvent");
        if (GetLastError() == ERROR_ALREADY_EXISTS) {
            if (opt_exclusive) {
                printf("event is already existed");
                return 1;
            }
            printf("Warning: open existing event, using -x to suppress");
        }
        ret = WaitForSingleObject(hEvent, opt_timeout);
        CloseHandle(hEvent);
        switch (ret) {
        case WAIT_OBJECT_0:             /* signaled */
            return 0;
        case WAIT_TIMEOUT:
            return 2;
        case WAIT_FAILED:
        default:
            return 3;
        }
        break;

    case 'm':
        printf("manual-reset isn't supported yet. ");
        break;

    case 'r':
    case 's':
        hEvent = OpenEvent(EVENT_ALL_ACCESS, /* desired access */
                           FALSE,       /* inherit handle */
                           opt_name);
        check(hEvent != NULL, "failed to OpenEvent");
        if (opt_mode == 'r')
            ret = ResetEvent(hEvent);
        else
            ret = SetEvent(hEvent);
        CloseHandle(hEvent);
        if (ret)
            return 0;
        else
            return 1;

    default:
        printf("illegal mode: %c\n", opt_mode);
        return 2;
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
    printf("winevent OPTIONS [EVENTNAME]\n"
           "\n"
           "Options: \n"
           "  -n EVENTNAME     specifies the event name\n"
           "  -w               wait once mode (default)\n"
           "  -m               manual-reset wait loop mode\n"
           "  -r               reset the event\n"
           "  -s               signal the event\n"
           "  -t TIMEOUT       wait timeout\n"
           );
}
