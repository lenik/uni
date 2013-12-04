#ifndef __BAS_LOG_H
#define __BAS_LOG_H

#include <syslog.h>

extern int syslog_option;
extern int syslog_facility;
extern int syslog_level;

void _log_x(const char *ident, int level, const char *format, ...);
void _log_x_perror(const char *ident, int level, const char *format, ...);

#ifndef log_ident
#define log_ident "user"
#endif

#define LOG_IF_LEVEL(level, args...) \
    if (syslog_level < level); else _log_x(log_ident, level, args)

#define log_emerg(...) LOG_IF_LEVEL(LOG_ERR, __VA_ARGS__)
#define log_alert(...) LOG_IF_LEVEL(LOG_ALERT, __VA_ARGS__)
#define log_crit(...) LOG_IF_LEVEL(LOG_CRIT, __VA_ARGS__)
#define log_err(...) LOG_IF_LEVEL(LOG_ERR, __VA_ARGS__)
#define log_warn(...) LOG_IF_LEVEL(LOG_WARNING, __VA_ARGS__)
#define log_notice(...) LOG_IF_LEVEL(LOG_NOTICE, __VA_ARGS__)
#define log_info(...) LOG_IF_LEVEL(LOG_INFO, __VA_ARGS__)
#define log_debug(...) LOG_IF_LEVEL(LOG_DEBUG, __VA_ARGS__)

#define LOG_PERROR_IF_LEVEL(level, ...) \
    if (syslog_level < level); else _log_x_perror(log_ident, level, __VA_ARGS__)

#define log_perr(...) LOG_PERROR_IF_LEVEL(LOG_ERR, __VA_ARGS__)
#define log_pwarn(...) LOG_PERROR_IF_LEVEL(LOG_WARNING, __VA_ARGS__)

#endif
