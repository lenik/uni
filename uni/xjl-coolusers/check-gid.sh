#!/bin/bash

etcdir="$1"
if [ ! -f "$etcdir/passwd" ]; then
    exit 0
fi
shift

err=0

while [ $# -ge 2 ]; do
    res_min=$1
    res_max=$2
    shift 2

    while IFS=: read name x gid users; do
        if [ -z "$gid" ]; then
            continue
        fi

        if [ $gid -ge $res_min ] && [ $gid -le $res_max ]; then
            echo "Reserved gid collision: $name ($gid)"
            ((err++))
        fi
    done <$etcdir/group
done

if [ $err != 0 ]; then
    exit 1
else
    exit 0
fi
