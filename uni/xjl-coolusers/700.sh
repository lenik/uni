#!/bin/bash

force=

if [ "$1" = '-f' ]; then
    force=1
fi


function getgid() {
    if l=`grep -m1 "^$gname:" /etc/group`; then
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
    local uname=$2
    local ugrp="$3"
    local ucmt="$4"
    local uhome="$5"
    local ucmd="$6"

    echo "Add user $uname ($uid)"

    if [ -z "$uhome" ]; then uhome=/home/$uname; fi
    if [ -z "$ucmd" ]; then ucmd=/bin/bash; fi

    if [ "$force" = 1 ]; then
        if grep -q "^$uname" /etc/passwd; then
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
    fi

    create_home=
    if [ ! -d "$uhome" ]; then
        create_home=--create-home
    fi

    if ! useradd \
        --uid     $uid \
        --groups  "$ugrp" --no-user-group \
        --home    "$uhome" $create_home \
        --comment "$ucmt" \
        --shell   "$ucmd" \
        $uname; then

        echo "  Failed to add user $uname ($uid)"
        exit 1
    fi

    return 0
}

# user 700-799

add_user 700 dev   dev  "Developer"
add_user 701 dev1  dev  "Developer 1"
add_user 702 dev2  dev  "Developer 1"

# user 800-899

add_user 800 play  play "Player"
add_user 801 play1 play "Player 1"
add_user 802 play2 play "Player 2"

add_user 810 demo  demo "Demo User"
add_user 811 demo1 demo "Demo User 1"
add_user 812 demo2 demo "Demo User 2"

# user 900-999

add_user 900 bind       - "Name daemon"         /var/cache/bind /bin/false
add_user 901 postfix    - "Postfix daemon"      /var/spool/postfix /bin/false

add_user 910 postgres   - "PostgreSQL admin"    /var/lib/postgresql
add_user 911 mysql      - "MySQL daemon"        /var/lib/mysql /bin/balse

add_user 920 appserv    - "App Server"          /home/appserv
