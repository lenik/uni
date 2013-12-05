#include <assert.h>
#include <errno.h>
#include <stdarg.h>
#include <stdio.h>                      /* sys_errlist, sys_nerr */
#include <string.h>
#include <syslog.h>
#include <bas/log.h>

int syslog_facility = LOG_USER;

void _log_x(const char *ident, int option, int level,
            const char *format, ...) {
    va_list ap;
    va_start(ap, format);

    openlog(ident, option, syslog_facility);
    vsyslog(level, format, ap);
    closelog();

    va_end(ap);
}

void _log_x_perror(const char *ident, int option, int level, const char *format, ...) {
    char msgbuf[1024];
    char *errmsg = "???";
    int errlen;

    if (errno >= 0 && errno < sys_nerr)
        errmsg = sys_errlist[errno];
    errlen = strlen(errmsg);

    va_list ap;
    va_start(ap, format);
    vsnprintf(msgbuf, sizeof(msgbuf) - (errlen + 2), format, ap);
    va_end(ap);

    strcat(msgbuf, ": ");
    strcat(msgbuf, errmsg);

    openlog(ident, option, syslog_facility);
    syslog(level, "%s", msgbuf);
    closelog();
}
