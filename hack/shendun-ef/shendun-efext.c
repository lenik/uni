#include <sys/types.h>
#include <sys/stat.h>
#include <assert.h>
#include <errno.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <bas/file.h>
#include <bas/str.h>

#include "ywcrypt.h"
#include "log.h"

/* .yw decoder */

void decode(char *buf, int size) {
    yw_decrypt(buf, size);
}

/* global gate */

#define AUTH_FILE       "@rundir@/sdefauth"
#define AUTH_MAC_MAX    99
#define FAILURE_MAX     5
#define RENEW_CMD       "./shendun-efpoll.ain -r"

struct auth_db {
    char mac[AUTH_MAC_MAX + 1];         /* (128-bit) hash of machine identity */
    long creation;                      /* authenticated start date */
    long expire;                        /* authenticated expire date */
    long now;                           /* current time in the auth server */
    int configs;                        /* re-config counter */
    int fails;                          /* communication failure counter */
    int serial;                         /* file serial (update counter) */
};

static struct auth_db db;
static time_t mtime;

static bool parse_auth(char *script, struct auth_db *db);

bool gate(pid_t pid) {
    struct stat sb;
    if (stat(AUTH_FILE, &sb) != 0) {
        if (errno == ENOENT) {          /* auth file isn't existed */
            int exit = system(RENEW_CMD);
            if (exit != 0) {
                log_err("Failed to execute the auth updater.");
                return false;
            }
        }
        /* other stat error. */
        log_perr("stat file %s", AUTH_FILE);
        return false;                  /* bad auth file: can't stat it. stop. */
    }

    if (sb.st_mtime > mtime) {          /* file changed, reload it anyway. */
        size_t size;
        char *auth_data = load_file(AUTH_FILE, &size, 0, 1);

        if (auth_data == NULL) {
            log_perr("Can't read from auth file.");
            return false;
        }
        auth_data[size] = '\0';

        yw_decrypt(auth_data, size);

        if (! parse_auth(auth_data, &db)) {
            log_err("Parse error in auth file.");
        } else {
            mtime = sb.st_mtime;        /* reloaded, okay. */
        }

        free(auth_data);
    }

    if (!strlen(db.mac) || db.expire == 0) {
        log_err("Software isn't registered, yet.");
        return false;
    }

    if (db.now > db.expire) {
        log_err("Registration is expired.");
        return false;
    }

    if (db.fails > FAILURE_MAX) {
        log_err("Too many network failures.");
        return false;
    }

    return true;
}

bool parse_auth(char *script, struct auth_db *db) {
    char *head = script;
    char *p;
    char *token;
    char *delim;
    char *arg;

    while ((p = strtok_eol(head, &head))) {
        token = readtok(&p);
        if (token == NULL)              /* empty line */
            continue;
        else
            rtrim(p);
        if (*token == '#')
            continue;                   /* comment line */

        delim = p ? readtok(&p) : NULL;
        assert(streq(delim, "="));

        switch (*token) {
        case 'a':
            if (streq("auth_configs", token)) {
                db->configs = (int) strtol(p, NULL, 0);
                break;
            }
            if (streq("auth_creation", token)) {
                db->creation = strtol(p, NULL, 0);
                break;
            }
            if (streq("auth_expire", token)) {
                db->expire = strtol(p, NULL, 0);
                break;
            }
            if (streq("auth_now", token)) {
                db->now = strtol(p, NULL, 0);
                break;
            }
            if (streq("auth_fails", token)) {
                db->fails = (int) strtol(p, NULL, 0);
                break;
            }
            if (streq("auth_mac", token)) {
                arg = readtok(&p);
                if (arg == NULL)
                    arg = "";
                strncpy(db->mac, arg, AUTH_MAC_MAX);
                break;
            }
            if (streq("auth_serial", token)) {
                db->serial = strtol(p, NULL, 0);
                break;
            }
            log_err("invalid auth var %s (= %s).", token, p);
            break;

        default:
            log_err("illegal syntax in auth data: %s %s.", token, p);
            return false;
        } /* switch: token */
    } /* while: strtok_eol */

    log_debug("Parsed auth file:");
    log_debug("    auth_mac      = %s", db->mac);
    log_debug("    auth_creation = %d", db->creation);
    log_debug("    auth_expire   = %d", db->expire);
    log_debug("    auth_now      = %d", db->now);
    log_debug("    auth_configs  = %d", db->configs);
    log_debug("    auth_fails    = %d", db->fails);
    log_debug("    auth_serial   = %d", db->serial);

    return true;
}

int main() {
    pid_t pid = getpid();
    bool status = gate(pid);
    if (status)
        printf("gate ok\n");
    else
        printf("gate fail\n");
    return 0;
}
