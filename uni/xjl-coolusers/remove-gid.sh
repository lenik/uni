#!/bin/bash

err=0
res_min=$1
res_max=$2

groups=

while IFS=: read name x gid members; do
    if [ -z "$gid" ]; then
        continue
    fi

    if [ $gid -ge $res_min ] && [ $gid -le $res_max ]; then
        groups="$groups $name"
    fi
done </etc/group

if [ -n "$groups" ]; then
    for g in $groups; do
        echo "Remove group $g"
        if ! groupdel $g; then
            ((err++))
        fi
    done
fi

if [ $err != 0 ]; then
    exit 1
else
    exit 0
fi
