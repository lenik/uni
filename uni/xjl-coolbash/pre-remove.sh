#!/bin/bash

sysconfdir=$1
if [ -z "$sysconfdir" ]; then
    echo sysconfdir is not specified.
    exit 1
fi

if [ -f $sysconfdir/bash.bashrc ]; then
    tmpout=/tmp/tmp.bashrc.$$.$RANDOM

    grep -v "\. $sysconfdir/bash_aliases\b" $sysconfdir/bash.bashrc >$tmpout
    if ! cmp -s $sysconfdir/bash.bashrc $tmpout; then
        echo Uninstall bash_aliases feature from bashrc.
        cp -f $tmpout $sysconfdir/bash.bashrc
    fi

    rm -f $tmpout
fi

exit 0
