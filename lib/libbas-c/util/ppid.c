#include <assert.h>
#include <limits.h>
#include <stdio.h>
#include <unistd.h>

#define LOG_IDENT "ppid"
#include <bas/cli.h>
#include <bas/proc.h>

static GOptionEntry options[] = {
    OPTION('s', "self", "Show self process"),
    OPTION('a', "all", "Show all parent processes"),
    /* Pipe to tac to reverse. */
    // OPTION('r', "reverse", "Show in reversed order"),
    OPTION('q', "quiet", "Show less verbose info"),
    OPTION('v', "verbose", "Show more verbose info"),
    OPTION(0, "version", "Show version info"),
    { NULL },
};

bool opt_self = false;
bool opt_all = false;
bool opt_norm = false;
pid_t opt_pid = (pid_t) 0;

void dump(pid_t pid);

gboolean parse_option(const char *_opt, const char *val,
                      gpointer data, GError **err) {
    const char *opt = _opt;

    bool shortopt = opt++[1] != '-';
    while (*opt == '-')
        opt++;

    switch (*opt) {
    case 's':
        if (shortopt || streq(opt, "self")) {
            opt_self = true;
            return true;
        }
        break;
        
    case 'a':
        if (shortopt || streq(opt, "all")) {
            opt_all = true;
            return true;
        }
        break;
    }

    return _parse_option(_opt, val, data, err);
}

int main(int argc, char **argv) {
    pid_t start;
    
    program_title = "Print parent process(-es) information";
    program_help_args = "[PID]";

    if (! parse_options(options, &argc, &argv))
        return 1;

    argc--;
    argv++;

    if (argc > 0)
        /* for each arg */
        while (argc-- > 0) {
            char *pidstr = *argv++;
            start = strtol(pidstr, NULL, 0);
            if (start == (pid_t) 0) {
                log_err("Bad pid: %s", pidstr);
                continue;
            }
            
            if (! opt_self) {
                start = getppidof(start);
                if (start == (pid_t) 0) {
                    log_warn("Maybe root process: %s", pidstr);
                    continue;
                }
            }
            
            log_info("Start-PID: %d", start);
            dump(start);
        }
    else {
        /* for current process */
        start = getpid();
        if (! opt_self) {
            start = getppidof(start);
            if (start == (pid_t) 0) {
                log_err("Failed to get the parent process for %d", getpid());
                return 1;
            }
        }
        dump(start);
    }
    return 0;
}

void dump(pid_t pid) {
    char sym[PATH_MAX];
    char exe[PATH_MAX];
    int cc, ch;
    FILE *f;
    pid_t ppid;
    
    printf("%d", pid);

    if (opt_norm) {
        sprintf(sym, "/proc/%d/exe", pid);
        cc = readlink(sym, exe, PATH_MAX);
        if (cc == -1)
            perror("\tReadlink failed");
        else
            printf("\t%s\n    ", exe);
    }
    
    sprintf(sym, "/proc/%d/cmdline", pid);
    if ((f = fopen(sym, "r")) == NULL) {
        fprintf(stderr, "\tFailed to open file %s", sym);
        perror("");
    } else {
        putchar('\t');
        while ((ch = fgetc(f)) != EOF) {
            putchar(ch);
        }
        fclose(f);
    }

    printf("\n");

    if (! opt_all)
        return;
    
    ppid = getppidof(pid);
    if (ppid == (pid_t) 0)
        return;
    
    dump(ppid);
}
