#include <windows.h>

#define d1 if (verb >= 1)
#define d2 if (verb >= 2)

static int verb = 1;

/*
int WINAPI WinMain(HINSTANCE hInst,
                   HINSTANCE hPrevInst,
                   LPSTR lpCmdLine,
                   int nCmdShow) {
*/

int main(int argc, char **argv) {
    int pargs = 1;
    char lpCmdLine[1000] = "";
    STARTUPINFO startup;
    PROCESS_INFORMATION procinf;
    BOOL ret;
    int pid;

    while (--argc) {
        ++argv;
        if (pargs) {
            if (!strcmp("-v", *argv) || !strcmp("--verbose", *argv))
                verb++;
            else if (!strcmp("-q", *argv) || !strcmp("--quiet", *argv))
                verb--;
            else
                pargs = 0;
            if (pargs)
                continue;
        }
        if (*lpCmdLine)
            strcat(lpCmdLine, " ");
        strcat(lpCmdLine, *argv);
    }

    d2 printf("create process: \"%s\"\n", lpCmdLine);

    memset(&startup, 0, sizeof(STARTUPINFO));
    startup.cb = sizeof(STARTUPINFO);
    ret = CreateProcess(NULL,           /* appname */
                        lpCmdLine,      /* cmdline */
                        NULL,           /* proc_attr */
                        NULL,           /* thread_attr */
                        FALSE,          /* inherit-handles */
                        0,              /* CREATE_NEW_PROCESS_GROUP */
                        NULL,           /* env */
                        NULL,           /* cwd */
                        &startup,       /* LPStartupInfo */
                        &procinf);      /*  */
    if (! ret) {
        printf("Error create process\n");
        return 0;
    }
    pid = (int)procinf.dwProcessId;
    d2 printf("pid = %d\n", pid);
    return pid;
}
