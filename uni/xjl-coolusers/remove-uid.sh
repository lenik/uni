#!/bin/bash

err=0
res_min=$1
res_max=$2

users=

while IFS=: read name x uid gid cmt home sh; do
    if [ -z "$uid" ]; then
        continue
    fi

    if [ $uid -ge $res_min ] && [ $uid -le $res_max ]; then
        users="$users $name"
    fi
done </etc/passwd

if [ -n "$users" ]; then
    for u in $users; do
        echo "Remove user $u"
        if ! userdel $u; then
            ((err++))
        fi
    done
fi

if [ $err != 0 ]; then
    exit 1
else
    exit 0
fi
