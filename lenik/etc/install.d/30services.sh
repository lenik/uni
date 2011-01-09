#!/bin/bash

if false; then
    NAMED=`findabc -p bind`
    NAMED_CONF=$NAMED/etc/named.conf
    echo "BIND: $NAMED ($NAMED_CONF)"
    sc create named binpath= "$NAMED -c $NAMED_CONF"
fi

if false; then
    APACHE2=`findabc httpd-2`
    APACHE2_EXEC=$APACHE2/bin/httpd.exe
    APACHE2_CONF=$LAPIOTA/etc/httpd.conf
    if [ ! -f "$APACHE2_CONF" ]; then
        APACHE2_CONF=$APACHE2/conf/httpd.conf
    fi
    # sc create httpd binpath= "$APACHE2_EXEC -k runservice"
    "$APACHE2_EXEC" -f "$APACHE2_CONF" -k install -n httpd
fi
