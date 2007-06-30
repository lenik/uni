#include <stdio.h>
#include <windows.h>

#define d1 if (verb >= 1)
#define d2 if (verb >= 2)

static int      verb        = 1;
static char *   pid_file    = 0;

/*
int WINAPI WinMain(HINSTANCE hInst,
                   HINSTANCE hPrevInst,
                   LPSTR lpCmdLine,
                   int nCmdShow) {
*/

int main(int argc, char **argv, char **env) {
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

    while (*env) {
        char *tok = strtok(*env, "=");
        if (strcmpi(tok, "pid_file") == 0) {
            pid_file = strtok(0, ";");
            break;
        }
        env++;
    }
    if (pid_file)
        d2 printf("pid file: \"%s\"\n", pid_file);
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

    if (pid_file) {
        FILE *f;
        d2 printf("writing to pid file %s\n", pid_file);
        if (f = fopen(pid_file, "wt")) {
            fprintf(f, "%d\n", pid);
            fclose(f);
        } else {
            perror("error open pid file");
        }
    }

    return pid;
}
