#include <sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <spawn.h>
#include <unistd.h>

#define wifork(ec, st) \
    do { \
        if (pid = fork()) \
            waitpid(pid, NULL, 0); \
        else { \
            st; \
            exit(ec); \
        } \
    } while (0)

void main() {
    pid_t pid;

    printf("User functions test:\n");

    printf("\nsystem(uname)\n");
    system("uname");
    system("echo Hey, this is an system cmd 'echo'.");

    printf("\nexecl(uname)\n");
    // TODO execl won't expand PATH environ,
    // so the absolute target is required.

    wifork(1, execl("/bin/uname", "fake name", "-a", NULL));
    wifork(1, execl("/bin/bash", "fake name", "-c", "echo oh, another echo.", NULL));

    printf("\nexeclp(uname)\n");
    wifork(1, execlp("uname", "fake name", "-a", NULL));
    wifork(1, execlp("bash", "fake name", "-c", "echo oh, i\\'m in the PATH..", NULL));

    printf("\nexecle(export | grep X)\n");
    wifork(1, execle("/bin/uname", "fake name", "-a", NULL));
    wifork(1, execle("/bin/bash", "fake name", "-c", "export | grep X", NULL,
        "X1=abc", "X2=def", NULL));

    printf("\nexeclpe(export | grep X)\n");
    wifork(1, execle("/bin/uname", "fake name", "-a", NULL));
    wifork(1, execle("/bin/bash", "fake name", "-c", "export | grep X", NULL,
        "X3=abc", "X4=def", NULL));

    printf("\nexecve(export | grep X)\n");
    {
        char *const argv1[] = { "fake name", "-a", NULL };
        char *const argv2[] = { "fake name", "-c", "export | grep X", NULL };
        char *const envv2[] = { "X10=cat", "X20=dog", NULL };
        wifork(1, execve("/bin/uname", argv1, NULL));
        wifork(1, execve("/bin/bash", argv2, envv2));
    }

    printf("\npopen(uname)\n");
    {
        FILE *p = popen("uname -a", "r");
        char buf[4096];
        if (p == NULL) {
            perror("Can't open the pipe");
            exit(1);
        }
        while (fgets(buf, sizeof(buf), p) != NULL) {
            printf(" .. %s", buf);
        }
        pclose(p);
    }
}