#!/bin/bash

sysconfdir=$1
if [ -z "$sysconfdir" ]; then
    echo sysconfdir is not specified.
    exit 1
fi

a_init=$sysconfdir/bash_aliases
echo -n "Remove $a_init from start up... "
    if lineconf -ekt $sysconfdir/bash.bashrc COOLBASH::a_init; then
        echo Done
    else
        echo Skipped
    fi

exit 0
