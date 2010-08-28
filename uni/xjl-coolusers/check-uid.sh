#!/bin/bash

err=0
res_min=$1
res_max=$2

while IFS=: read name x uid gid cmt home sh; do
    if [ -z "$uid" ]; then
        continue
    fi

    if [ $uid -ge $res_min ] && [ $uid -le $res_max ]; then
        echo "Reserved uid collision: $name ($uid)"
        ((err++))
    fi
done </etc/passwd

if [ $err != 0 ]; then
    exit 1
else
    exit 0
fi
