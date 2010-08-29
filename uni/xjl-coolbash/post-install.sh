#!/bin/sh

sysconfdir=$1
if [ -z "$sysconfdir" ]; then
    echo sysconfdir is not specified.
    exit 1
fi

if ! grep -q "\. $sysconfdir/bash_aliases\b" $sysconfdir/bash.bashrc 2>/dev/null; then
    echo "\
if [ -f $sysconfdir/bash_aliases ]; then \
    . $sysconfdir/bash_aliases; \
fi" >>$sysconfdir/bash.bashrc
fi

skel_rc=/etc/skel/.bashrc
if [ -f $skel_rc ]; then
    tmp=/tmp/bashrc-repl-$$.$RANDOM
    egrep -v "\bHIST(\w+)\b" $skel_rc >$tmp
    if ! cmp -s $skel_rc $tmp; then
        mv -f $tmp $skel_rc
    fi
fi
