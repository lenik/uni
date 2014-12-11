#include <sys/types.h>
#include <limits.h>
#include <stdio.h>

#include <bas/proc.h>
#include <bas/compat/scanf.h>

pid_t getppidof(pid_t pid) {
    char path[PATH_MAX];
    FILE *f;
    char comm[PATH_MAX];
    char state;
    pid_t ppid;

    sprintf(path, "/proc/%d/stat", pid);
    if ((f = fopen(path, "rt")) == NULL)
        return -1;

    fscanf(f, "%d %s %c %d", &pid, comm, &state, &ppid);
    fclose(f);

    return ppid;
}
