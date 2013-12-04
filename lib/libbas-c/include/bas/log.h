#ifndef __BAS_LOG_H
#define __BAS_LOG_H

extern int syslog_option;
extern int syslog_facility;
extern int syslog_level;

void log_ident(const char *ident);

void log_emerg(const char *format, ...);
void log_alert(const char *format, ...);
void log_crit(const char *format, ...);
void log_err(const char *format, ...);
void log_warn(const char *format, ...);
void log_notice(const char *format, ...);
void log_info(const char *format, ...);
void log_debug(const char *format, ...);

void log_perr(const char *format, ...);
void log_pwarn(const char *format, ...);

#endif
