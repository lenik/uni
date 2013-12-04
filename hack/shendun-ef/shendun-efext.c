#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <bas/file.h>
#include <bas/str.h>

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
    char mac[16];             /* (128-bit) hash of machine identity */
    int registered;           /* the mac code is registered somewhere remote. */
    int acc;                  /* used time accumulator, in seconds. */
    int alloc;                /* allocated time, in seconds. */
    int failure;              /* communication failure counter */
};

static bool parse_auth(char *script, struct auth_db *db) {
    char *head = script;
    char *p;
    char *token;

    while ((p = strtok_eol(head, &head))) {
        token = readtok(&p);
        if (token == NULL)              /* empty line */
            continue;
        else
            rtrim(p);

        switch (*token) {
        case '#':
            continue;                   /* comment line */

        case 'm':
            if (streq("mac", token)) {

            }
            break;

        default:
            fprintf(stderr, "Error: illegal syntax in auth data: %s %s.\n",
                    token, p);
            return false;
        } /* switch: token */
    } /* while: strtok_eol */
    return true;
}

bool gate(pid_t pid) {
    static struct auth_db db;
    static time_t mtime;

    struct stat sb;
    if (stat(AUTH_FILE, &sb) != 0) {
        if (errno == ENOENT) {          /* auth file isn't existed */
            int exit = system(RENEW_PROGRAM);
            if (exit != 0) {
                fprintf(stderr, "Failed to execute the auth updater.\n");
                return false;
            }
        }
        /* other stat error. */
        perror("stat");
        return false;                  /* bad auth file: can't stat it. stop. */
    }

    if (sb.st_mtime > mtime) {          /* file changed, reload it anyway. */
        if (! auth_reload())            /* became illegal... */
            return false;

        stat(AUTH_FILE, &sb);

        size_t size;
        char *auth_data = load_file(AUTH_FILE, &size, 0, 1);

        if (auth_data == NULL) {
            perror("Can't load auth file");
            return false;
        }
        auth_data[size] = '\0';

        yw_decrypt(auth_data, size);

        if (! parse_auth(auth_data, &db)) {
            fprintf(stderr, "Parse error in auth file.\n");
        } else {
            mtime = sb.st_mtime;        /* reloaded, okay. */
        }

        free(auth_data);
    }

    if (db.acc > db.alloc) {            /* allocated time is used up. */
        fprintf(stderr, "Registration expired.\n");
        return false;
    }

    if (! db.registered) {
        fprintf(stderr, "Software isn't registered, yet.\n");
        return false;
    }

    return true;
}
