#include <assert.h>
#include <errno.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>                      /* sys_errlist, sys_nerr */
#include <string.h>
#include <time.h>

#include <bas/log.h>
#include <bas/csi.h>                    /* CSI escape codes */

#define LOG_FILE stderr
#define COLOR if (option & LOG_COLOR)

int log_level = 1;

/* format: "2000-01-01 01:12:34 [ident] ERROR ... strerror" */

void _vlog_x(const char *ident, int option, int level,
             const char *format, va_list ap) {
    int errnum = errno;                 /* save the errno immediately. */
    bool dirty = false;

    if (option & LOG_DATETIME) {
        time_t t = time(NULL);
        struct tm tm;
        char buf[200] = "strftime error";

        COLOR fputs(CSI FG_DARKGRAY SGR, LOG_FILE);

        if (localtime_r(&t, &tm) == NULL) {
            fprintf(LOG_FILE, "localtime: %s", strerror(errno));
        } else {
            if (option & LOG_DATE) {
                strftime(buf, sizeof(buf), "%Y-%m-%d", &tm);
                fputs(buf, LOG_FILE);
            }

            if (option & LOG_TIME) {
                if (option & LOG_DATE)
                    fputc(' ', LOG_FILE);
                strftime(buf, sizeof(buf), "%H:%M:%S", &tm);
                fputs(buf, LOG_FILE);
            }
        }

        COLOR fputs(CSI RESET SGR, LOG_FILE);
        dirty = true;
    }

    /* ident */
    if (ident && *ident) {
        if (dirty)
            fputc(' ', LOG_FILE);

        COLOR fputs(CSI FG_WHITE BG_BROWN SGR, LOG_FILE);

        fprintf(LOG_FILE, "[%s]", ident);

        COLOR fputs(CSI RESET SGR, LOG_FILE);
        dirty = true;
    }

    /* level */
    const char *level_name = "_Level";
    const char *level_color = "";

    if (option & (LOG_LEVNAME | LOG_COLOR))
        switch (level) {
        case -4:
        case -3:
        case -2:
            level_name = "FATAL";
            level_color = CSI BLINK FG_WHITE BG_RED SGR;
            break;
        case -1:
            level_name = "ERROR";
            level_color = CSI FG_LIGHTRED SGR;
            break;
        case 0:
            level_name = "WARN";
            level_color = CSI FG_LIGHTMAGENTA SGR;
            break;
        case 1:
            level_name = "NOTICE";
            level_color = CSI BOLD SGR;
            break;
        case 2:
            level_name = "INFO";
            level_color = CSI FG_DARKGRAY SGR;
            break;
        case 3:
            level_name = "DEBUG";
            level_color = CSI FG_GRAY _DARK SGR;
            break;
        case 4:
            level_name = "TRACE";
            level_color = CSI FG_GRAY _DARK SGR;
            break;
        default:
            if (level < -3)
                level_name = "FATAL";
            if (level > 3)
                level_name = "DEBUG";
        }

    if (dirty)
        fputc(' ', LOG_FILE);
    COLOR fputs(level_color, LOG_FILE);

    if (option & LOG_LEVNAME) {
        fputs(level_name, LOG_FILE);
        fputs(":", LOG_FILE);
        dirty = true;
    }

    /* message */
    if (dirty)
        fputc(' ', LOG_FILE);
    vfprintf(LOG_FILE, format, ap);
    COLOR fputs(CSI RESET SGR, LOG_FILE);

    /* strerror */
    if (option & LOG_STRERROR) {
        fputc(' ', LOG_FILE);
        COLOR fputs(CSI UNDERLINE FG_RED SGR, LOG_FILE);
        fputs(strerror(errnum), LOG_FILE);
        COLOR fputs(CSI RESET SGR, LOG_FILE);
    }

    if (option & LOG_CR)
        fputc('\r', LOG_FILE);
    else
        fputc('\n', LOG_FILE);

    errno = errnum;                     /* restore the errno */
}

void _log_x(const char *ident, int option, int level,
             const char *format, ...) {
    va_list ap;
    va_start(ap, format);
    _vlog_x(ident, option, level, format, ap);
    va_end(ap);
}
