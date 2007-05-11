#include <windows.h>

#define d1 if (verb >= 1)
#define d2 if (verb >= 2)

#define ERR_OPENPROCTOK 101
#define ERR_LOOKPRIV    102
#define ERR_ADJUSTPRIV  103
#define ERR_OPENPROCESS 104
#define ERR_WAIT        105

static int verb = 1;

int main(int argc, char **argv) {
    DWORD ret;
    int pid;
    HANDLE hproctok;
    LUID luid;
    TOKEN_PRIVILEGES priv;
    HANDLE hproc;

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

    d2 printf("pid: %d\n", pid);

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
        printf("can't open process: %d\n", GetLastError());
        return ERR_OPENPROCESS;
    }

    ret = WaitForSingleObject(hproc, INFINITE);
    if (ret == WAIT_FAILED) {
        printf("wait failed: %d\n", GetLastError());
        return ERR_WAIT;
    }

    CloseHandle(hproc);
    return (int)ret;
}
