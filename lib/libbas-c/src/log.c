#include <assert.h>
#include <stdarg.h>
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
    va_list ap;
    va_start(ap, format);

    openlog(ident, option, syslog_facility);
    vsyslog(level, format, ap);
    closelog();

    va_end(ap);

    perror("");
}
