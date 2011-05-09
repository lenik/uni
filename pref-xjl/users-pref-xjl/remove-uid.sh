#!/bin/bash

etcdir="$1"
if [ ! -f "$etcdir/passwd" ]; then
    exit 0
fi
shift

err=0

users=
while [ $# -ge 2 ]; do
    res_min=$1
    res_max=$2
    shift 2

    while IFS=: read name x uid gid cmt home sh; do
        if [ -z "$uid" ]; then
            continue
        fi

        if [ $uid -ge $res_min ] && [ $uid -le $res_max ]; then
            users="$users $name"
        fi
    done <$etcdir/passwd
done

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
