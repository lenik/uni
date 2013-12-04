#include <assert.h>
#include <stdarg.h>
#include <string.h>
#include <syslog.h>
#include <bas/log.h>

#define SYSLOG_IDENT_MAX 63
#define SYSLOG_IDENT_DEFAULT "user"

char syslog_ident[SYSLOG_IDENT_MAX + 1] = SYSLOG_IDENT_DEFAULT;
int syslog_option = LOG_PERROR;
int syslog_facility = LOG_USER;

int syslog_level = LOG_NOTICE;

void log_ident(const char *ident) {
    if (ident == NULL)
        ident = SYSLOG_IDENT_DEFAULT;

    strncpy(syslog_ident, ident, SYSLOG_IDENT_MAX);
}

static void do_log(int level, const char *format, va_list ap) {
    openlog(syslog_ident, syslog_option, syslog_facility);
    vsyslog(LOG_WARNING, format, ap);
    closelog();
}

#define LOG_IMPL(fn, level)            \
    void fn(const char *format, ...) { \
        LOG_IMPL_BODY(level);          \
    }

#define LOG_IMPL_BODY(level)           \
    do {                               \
        if (syslog_level < level)      \
            return;                    \
        va_list ap;                    \
        va_start(ap, format);          \
        do_log(level, format, ap);     \
        va_end(ap);                    \
    } while (0)

LOG_IMPL(log_emerg, LOG_EMERG)
LOG_IMPL(log_alert, LOG_ALERT)
LOG_IMPL(log_crit, LOG_CRIT)
LOG_IMPL(log_err, LOG_ERR)
LOG_IMPL(log_warn, LOG_WARNING)
LOG_IMPL(log_notice, LOG_NOTICE)
LOG_IMPL(log_info, LOG_INFO)
LOG_IMPL(log_debug, LOG_DEBUG)

void log_perr(const char *format, ...) {
    LOG_IMPL_BODY(LOG_ERR);
    perror("");
}

void log_pwarn(const char *format, ...) {
    LOG_IMPL_BODY(LOG_WARNING);
    perror("");
}
