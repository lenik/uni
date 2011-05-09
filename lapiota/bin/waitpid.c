#include <windows.h>

#define d1 if (verb >= 1)
#define d2 if (verb >= 2)

#define ERR_OPENPROCTOK 101
#define ERR_LOOKPRIV    102
#define ERR_ADJUSTPRIV  103
#define ERR_OPENPROCESS 0
#define ERR_WAIT        104
#define ERR_SIGNALED    0
#define ERR_TIMEOUT     3

static int verb         = 1;
static int timeout      = INFINITE;

void help() {
    printf("waitpid <pid> [timeout]\n");
}

int main(int argc, char **argv) {
    DWORD               ret;
    int                 pid = -1;
    HANDLE              hproctok;
    LUID                luid;
    TOKEN_PRIVILEGES    priv;
    HANDLE              hproc;

    while (--argc) {
        ++argv;
        if (!strcmp("-v", *argv) || !strcmp("--verbose", *argv))
            verb++;
        else if (!strcmp("-q", *argv) || !strcmp("--quiet", *argv))
            verb--;
        else {
            pid = (int) strtol(*argv, NULL, 0);
            break;
        }
    }

    if (pid == -1) {
        help();
        return -1;
    }
    if (argc > 1) {
        --argc;
        ++argv;
        timeout = (int) strtol(*argv, NULL, 0);
    }
    d2 printf("pid: %d\n", pid);
    d2 printf("timeout: %d\n", timeout);

    /*
    if (! OpenProcessToken(GetCurrentProcess(),
                           TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY,
                           &hproctok)) {
        return ERR_OPENPROCTOK;
    }

    if (! LookupPrivilegeValue(NULL, SE_SHUTDOWN_NAME, &luid)) {
        CloseHandle(hproctok);
        return ERR_LOOKPRIV;
    }

    priv.PrivilegeCount = 1;
    priv.Privileges[0].Luid = luid;
    priv.Privileges[0].Attribute = SE_PRIVILEGE_ENABLED;
    if (! AdjustTokenPrivileges(hproctok,
                                FALSE,
                                &priv,
                                NULL, NULL, NULL)) {
        CloseHandle(hproctok);
        return ERR_ADJUSTPRIV;
    }

    CloseHandle(hproctok);
    */

    hproc = OpenProcess(SYNCHRONIZE,    /* desired access */
                        FALSE,          /* inheritable */
                        pid);
    if (hproc == NULL) {
        d1 printf("can't open process: error %d\n", GetLastError());
        return ERR_OPENPROCESS;
    }

    ret = WaitForSingleObject(hproc, timeout);
    switch (ret) {
    case WAIT_OBJECT_0:
        ret = ERR_SIGNALED;
        break;
    case WAIT_TIMEOUT:
        ret = ERR_TIMEOUT;
        break;
    case WAIT_FAILED:
        d1 printf("wait failed: error %d\n", GetLastError());
    default:
        ret = ERR_WAIT;
    }

    CloseHandle(hproc);
    return (int)ret;
}
