#!/bin/bash

err=0
res_min=$1
res_max=$2

while IFS=: read name x gid users; do
    if [ -z "$gid" ]; then
        continue
    fi

    if [ $gid -ge $res_min ] && [ $gid -le $res_max ]; then
        echo "Reserved gid collision: $name ($gid)"
        ((err++))
    fi
done </etc/group

if [ $err != 0 ]; then
    exit 1
else
    exit 0
fi
