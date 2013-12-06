#ifndef __BAS_LOG_H
#define __BAS_LOG_H

#define LOG_STRERROR 0x0001             /* append the errno string */
#define LOG_COLOR    0x0010             /* enable ANSI color fx */
#define LOG_CR       0x0020             /* end by CR instead of NL */
#define LOG_LEVNAME  0x0100             /* print the level name */
#define LOG_DATE     0x0400             /* print the logging date */
#define LOG_TIME     0x0800             /* print the logging time */
#define LOG_DATETIME (LOG_DATE | LOG_TIME)

/* the runtime logging level. */
extern int log_level;

void _log_x(const char *ident, int option, int level,
            const char *format, ...);

#ifndef LOG_IDENT
#define LOG_IDENT ""
#endif

#ifndef LOG_OPTION
#define LOG_OPTION (LOG_LEVNAME | LOG_COLOR)
#endif

/* This is the logging level configured for each source file. */
#ifndef LOG_LEVEL
#  ifdef DEBUG
#    define LOG_LEVEL 3
#  else
#    define LOG_LEVEL 1
#  endif
#endif

#define LOG_IF_LEVEL(level, ...) \
    if (!(level <= LOG_LEVEL && level <= log_level)); \
    else _log_x(LOG_IDENT, LOG_OPTION, level, __VA_ARGS__)

#define LOG_PERROR_IF_LEVEL(level, ...) \
    if (!(level <= LOG_LEVEL && level <= log_level)); \
    else _log_x(LOG_IDENT, LOG_OPTION | LOG_STRERROR, level, __VA_ARGS__)

#define log_emerg(...)  LOG_IF_LEVEL(-4, __VA_ARGS__)
#define log_alert(...)  LOG_IF_LEVEL(-3, __VA_ARGS__)
#define log_crit(...)   LOG_IF_LEVEL(-2, __VA_ARGS__)
#define log_err(...)    LOG_IF_LEVEL(-1, __VA_ARGS__)
#define log_warn(...)   LOG_IF_LEVEL(0, __VA_ARGS__)
#define log_notice(...) LOG_IF_LEVEL(1, __VA_ARGS__)
#define log_info(...)   LOG_IF_LEVEL(2, __VA_ARGS__)
#define log_debug(...)  LOG_IF_LEVEL(3, __VA_ARGS__)
#define log_trace(...)  LOG_IF_LEVEL(4, __VA_ARGS__)

#define log_perr(...) LOG_PERROR_IF_LEVEL(-1, __VA_ARGS__)
#define log_pwarn(...) LOG_PERROR_IF_LEVEL(0, __VA_ARGS__)

#endif
