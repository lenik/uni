#include <sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <spawn.h>
#include <unistd.h>

#define LOG_IDENT "execfilter/user"
#define LOG_OPTION LOG_PERROR
#define LOG_LEVEL LOG_DEBUG
#include <bas/log.h>

#define wifork(ec, st) \
    do { \
        if (pid = fork()) \
            waitpid(pid, NULL, 0); \
        else { \
            st; \
            exit(ec); \
        } \
    } while (0)

int main() {
    pid_t pid;

    log_notice("User functions test:\n");

    log_notice("system(non-exist):");
    system("/bin/non-exist");
    printf("\n");

    log_notice("system(uname):");
    system("uname");
    log_notice("system(echo ...):");
    system("echo Hey, this is an system cmd 'echo'.");
    // system("sh -c 'export | grep -n X'");
    printf("\n");

    log_notice("execl(uname):");         /* execl won't expand PATH. */
    wifork(1, execl("/bin/uname", "fake name", "-a", NULL));
    log_notice("execl(bash -c echo ...):");         /* execl won't expand PATH. */
    wifork(1, execl("/bin/bash", "fake name", "-c", "echo oh, another echo.", NULL));
    printf("\n");

    log_notice("execlp(uname):");
    wifork(1, execlp("uname", "fake name", "-a", NULL));
    log_notice("execlp(bash -c echo ...):");
    wifork(1, execlp("bash", "fake name", "-c", "echo oh, i\\'m in the PATH..", NULL));
    printf("\n");

    {
        char *const envv[] = { "X1=abc", "X2=def", NULL };
        log_notice("execle(uname):");
        wifork(1, execle("/bin/uname", "fake name", "-a", NULL, NULL));
        log_notice("execle(sh -c 'export | grep -n X'):");
        wifork(1, execle("/bin/bash", "fake name", "-c", "export | grep -n X", NULL,
            envv));
    }
    printf("\n");

    {
        char *const argv1[] = { "fake name", "-a", NULL };
        char *const argv2[] = { "fake name", "-c", "export | grep -n X", NULL };
        char *const envv2[] = { "X10=cat", "X20=dog", NULL };
        log_notice("execve(uname):");
        wifork(1, execve("/bin/uname", argv1, NULL));
        log_notice("execve(sh -c 'export | grep -n X):");
        wifork(1, execve("/bin/bash", argv2, envv2));
    }
    printf("\n");

    log_notice("popen(uname):");
    {
        FILE *p = popen("uname -a", "r");
        char buf[4096];
        if (p == NULL) {
            log_perr("Can't open the pipe");
            exit(1);
        }
        while (fgets(buf, sizeof(buf), p) != NULL) {
            log_notice("Pipe/Input: %s", buf);
        }
        pclose(p);
    }
    printf("\n");

    return 0;
}
