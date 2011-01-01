#!/bin/bash

etcdir="$1"
if [ ! -f "$etcdir/passwd" ]; then
    exit 0
fi
shift

force=
if [ "$1" = '-f' ]; then
    force=1
fi

# group 70-99

groupadd -g70 dev
groupadd -g71 play
groupadd -g72 demo

# user 70-99
