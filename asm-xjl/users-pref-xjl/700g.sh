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

# group 700-799

groupadd -g700 dev
groupadd -g701 play
groupadd -g702 demo

