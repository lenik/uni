
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <tlhelp32.h>

#pragma comment(lib, "toolhelp.lib")

#define d1 if (verb >= 1)
#define d2 if (verb >= 2)

static int      verb        = 1;
static int      stack       = 0;

void errmsg(DWORD err) {
    LPVOID lpMsgBuf;
    FormatMessage(
        FORMAT_MESSAGE_ALLOCATE_BUFFER |
        FORMAT_MESSAGE_FROM_SYSTEM |
        FORMAT_MESSAGE_IGNORE_INSERTS,
        NULL,
        err,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
        (LPTSTR) &lpMsgBuf,
        0,
        NULL
    );
    printf("err %x(%d): %s\n", err, err, (char*)lpMsgBuf);
    LocalFree(lpMsgBuf);
}

int pid_info(int pid, PROCESSENTRY32 *ent) {
    HANDLE  h   = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    BOOL    cont;
    ent->dwSize = sizeof(PROCESSENTRY32);
    for (cont = Process32First(h, ent); cont; cont = Process32Next(h, ent)) {
        if (ent->th32ProcessID == pid)
            break;
        ent->dwSize = sizeof(PROCESSENTRY32);
    }
    CloseHandle(h);
    return ent->th32ProcessID == pid;
}

int parent_pid(int pid) {
    PROCESSENTRY32  ent;
    if (pid_info(pid, &ent))
        return ent.th32ParentProcessID;
    return -1;
}

int main(int argc, char **argv) {
    int pid = GetCurrentProcessId();
    int parent;
    while (--argc) {
        ++argv;
        if (!strcmp("-v", *argv) || !strcmp("--verbose", *argv))
            verb++;
        else if (!strcmp("-q", *argv) || !strcmp("--quiet", *argv))
            verb--;
        else if (!strcmp("-s", *argv) || !strcmp("--stack", *argv))
            stack = 1;
        else
            break;
    }
    if (argc) {
        pid = strtol(*argv, 0, 0);
        --argc; ++argv;
    }
    d2 printf("pid=%d\n", pid);

    parent = parent_pid(pid);
    d2 printf("parent=%d\n", parent);

    if (stack) {
        /* Print "calling-stack" */
        PROCESSENTRY32 ent;
        while (pid_info(pid, &ent)) {
            printf("%d\t%s\n", pid, ent.szExeFile);
            pid = ent.th32ParentProcessID;
        }
    }

    return parent;
}
