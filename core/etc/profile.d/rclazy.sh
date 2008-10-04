
function start_syslog() {
    local SYSLOG=
    if [ -f /var/run/syslog.pid ]; then
        read SYSLOG </var/run/syslog.pid
        kill -s SIGHUP $SYSLOG 2>/dev/null || unset SYSLOG
    fi
    if [ -z $SYSLOG ]; then
        logger -s syslogd started.
        syslogd
    fi
}
start_syslog; unset start_syslog

cron 2>/dev/null && logger -s cron started.
