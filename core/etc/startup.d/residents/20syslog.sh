# syslogd

if [ -f /var/run/syslog.pid ]; then
    read syslog </var/run/syslog.pid
    kill $syslog 2>/dev/null
fi

syslogd
