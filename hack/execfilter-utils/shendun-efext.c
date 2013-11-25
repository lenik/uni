#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <stdio.h>
#include <unistd.h>

#include "ywcrypt.h"

#define AUTH_FILE "@cachedir@/auth.dat"
#define RENEW_PROGRAM "shendun-renew"
#define FAILURE_MAX 5

/* .yw decoder */

void decode(char *buf, int size) {
    yw_decrypt(buf, size);
}

/* global gate */

struct auth_db {
    char mac[8];              /* hash of machine identity */
    int registered;           /* the mac code is registered somewhere remote. */
    int acc;                  /* used time accumulator, in seconds. */
    int alloc;                /* allocated time, in seconds. */
    int failure;              /* communication failure counter */
};

int gate(pid_t pid) {
    static struct auth_db db;
    static time_t mtime;

    struct stat sb;
    if (stat(AUTH_FILE, &sb) != 0) {
        if (errno == ENOENT) {          /* file isn't existed */
            int exit = system(RENEW_PROGRAM);
            if (exit != 0) {
                fprintf(stderr, "Failed to execute the auth updater.\n");
                return 0;
            }
        }
        /* other stat error. */
        perror("stat");
        return 0;                       /* bad auth file, stop. */
    }

    if (sb.st_mtime > mtime) {          /* file changed, reload it anyway. */
        if (! auth_reload())            /* became illegal... */
            return 0;

        stat(AUTH_FILE, &sb);
        mtime = sb.st_mtime;            /* reloaded, okay. */
    }

    if (db.acc > db.alloc) {            /* allocated time is used up. */
        fprintf(stderr, "Registration expired.\n");
        return 0;
    }

    if (! db.registered) {
        fprintf(stderr, "Software isn't registered, yet.\n");
        return 0;
    }

    return 1;
}
