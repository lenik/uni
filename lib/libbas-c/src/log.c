#include <assert.h>
#include <stdarg.h>
#include <string.h>
#include <syslog.h>
#include <bas/log.h>

int syslog_option = LOG_PERROR;
int syslog_facility = LOG_USER;
int syslog_level = LOG_NOTICE;

void _log_x(const char *ident, int level, const char *format, ...) {
    // if (syslog_level < level) return;
    va_list ap;
    va_start(ap, format);

    openlog(ident, syslog_option, syslog_facility);
    vsyslog(level, format, ap);
    closelog();

    va_end(ap);
}

void _log_x_perror(const char *ident, int level, const char *format, ...) {
    // if (syslog_level < level) return;
    va_list ap;
    va_start(ap, format);

    openlog(ident, syslog_option, syslog_facility);
    vsyslog(level, format, ap);
    closelog();

    va_end(ap);

    perror("");
}
