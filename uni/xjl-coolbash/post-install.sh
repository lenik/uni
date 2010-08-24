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
