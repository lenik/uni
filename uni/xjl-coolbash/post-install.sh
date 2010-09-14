#!/bin/sh

sysconfdir=$1
if [ -z "$sysconfdir" ]; then
    echo sysconfdir is not specified.
    exit 1
fi

a_init=$sysconfdir/bash_aliases
echo -n "Add $a_init to start up... "
    if lineconf -et $sysconfdir/bash.bashrc \
            COOLBASH::a_init "if [ -f $a_init ]; then . $a_init; fi"
    then
        echo Done
    else
        echo Skipped
    fi

skel_rc=/etc/skel/.bashrc
echo -n "Remove HIST-overrides from $skel_rc... "
    if [ -f $skel_rc ]; then
        tmp=/tmp/bashrc-repl-$$.$RANDOM
        egrep -v "\bHIST(\w+)\b" $skel_rc >$tmp
        if ! cmp -s $skel_rc $tmp; then
            mv -f $tmp $skel_rc
            echo Done
        else
            rm -f $tmp
            echo Skipped
        fi
    else
        echo Skipped
    fi
