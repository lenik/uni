#!/bin/bash

# Add 800-series users&groups.

force=
if [ "$1" = '-f' ]; then
    force=1
    shift
fi

etcdir="$1"
if [ ! -f "$etcdir/passwd" ]; then
    exit 0
fi
shift

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
    shift 4; shift; shift

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

    local groups="$ugrp"
    for a in "$@"; do
        groups="$groups,$a"
    done

    if ! useradd \
        --uid     $uid \
        --gid     $gid \
        --groups  "$groups" --no-user-group \
        --home    "$uhome" $create_home \
        --comment "$ucmt" \
        --shell   "$ucmd" \
        $uname; then

        echo "  Failed to add user $uname ($uid)"
        return 1
    fi

    return 0
}

# user 800-899
    add_user 800 appserv    dev  "App Server"           /home/appserv
    add_user 801 scm        dev  "VCS daemon"           /none /bin/false

# user 900-999
    add_user 900 play       play "Player"
    add_user 901 demo       demo "Demo User"
    add_user 999 dev        dev "Developer"             /home/dev /bin/bash \
        admin \
        cdrom \
        dev \
        dialout \
        disk \
        lpadmin \
        netdev \
        plugdev \
        vboxusers \
        video

exit 0
