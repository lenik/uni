#!/bin/bash

# Add 700-series users&groups.

etcdir="$1"
if [ ! -f "$etcdir/passwd" ]; then
    exit 0
fi
shift

force=
if [ "$1" = '-f' ]; then
    force=1
fi


function getgid() {
    local gname="$1"
    if l=`grep -m1 "^$gname:" $etcdir/group`; then
        l="${l#*:}"
        l="${l#*:}"
        echo ${l%%:*}
        return 0
    else
        return 1
    fi
}

function add_user() {
    local uid=$1
    local gid=
    local uname=$2
    local ugrp="$3"
    local ucmt="$4"
    local uhome="$5"
    local ucmd="$6"

    echo "Add user $uname ($uid)"

    if [ -z "$uhome" ]; then uhome=/home/$uname; fi
    if [ -z "$ucmd" ]; then ucmd=/bin/bash; fi

    if [ "$force" = 1 ]; then
        if grep -q "^$uname" $etcdir/passwd; then
            echo "  Delete existing user $uname"
            userdel "$uname"            # may delete the group also.
        fi
    fi

    if [ -z "$ugrp" ] || [ "$ugrp" = '-' ]; then
        gid=$uid
        ugrp=$uname

        # ignore whether group is existed.
        echo "  Add user group $ugrp ($gid)"
        groupadd -g$gid $ugrp
    else
        firstgrp=${ugrp%%,*}
        gid=`getgid $firstgrp`
    fi

    create_home=
    if [ ! -d "$uhome" ]; then
        create_home=--create-home
    fi

    if ! useradd \
        --uid     $uid \
        --gid     $gid \
        --groups  "$ugrp" --no-user-group \
        --home    "$uhome" $create_home \
        --comment "$ucmt" \
        --shell   "$ucmd" \
        $uname; then

        echo "  Failed to add user $uname ($uid)"
        return 1
    fi

    return 0
}

# user 700-799

add_user 700 bind     -    "Name daemon"         /var/cache/bind /bin/false
add_user 701 postfix  -    "Postfix daemon"      /var/spool/postfix /bin/false

add_user 710 postgres -    "PostgreSQL admin"    /var/lib/postgresql
add_user 711 mysql    dev  "MySQL daemon"        /var/lib/mysql /bin/balse

add_user 720 appserv  dev  "App Server"          /home/appserv

# user 800-999

add_user 900 play     play "Player"
add_user 910 demo     demo "Demo User"

add_user 999 dev      dev,admin,postgres "Developer"

exit 0
