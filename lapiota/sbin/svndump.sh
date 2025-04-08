#!/bin/bash

shopt -s nullglob

function recreate() {
    local src=$1
    local tmp=$2
    local dst=$3
    local stage=$4
    if [ ! -f "$src/db/current" ]; then
        echo bad source repo $src.
    fi
    read CURRENT what else <"$src/db/current"
    if [ "$CURRENT" -lt 1 ]; then
        echo no rev.
        exit 1
    fi

    if [ "$stage" = load ]; then
        echo continue to load $tmp.
    else
        for ((i = 1; i <= CURRENT; i++)); do
            svnadmin dump -r $i --incremental "$src" >"$tmp/$i"
        done
    fi

    if [ -d "$dst" ]; then
        echo "target repo $dst is already existed, overwrite? (y/n)"
        read force
        if [ "$force" != "y" ]; then
            echo user canceled.
            exit 1
        fi
        rm -fR "$dst"
    fi
    svnadmin create "$dst"

    for ((i = 1; i <= CURRENT; i++)); do
        if [ $i = 1 ]; then
            loadopt=--ignore-uuid
        else
            loadopt=
        fi
        # grep -a "^Revision-number: " "$tmp/$i" >"$tmp/orig"
        # read _ orig _ <"$tmp/orig"
        # echo "* Load revision $i (from original revision $orig)."
        svnadmin load "$dst" $loadopt <"$tmp/$i" >"$tmp/log"
        cat "$tmp/log" >>"$dst/loadlog"

        grep "^------- Committed .*>>>$" "$tmp/log" >"$tmp/log1"
        # - - new rev 12 (loaded from original rev 13) >>>
        read _ _ mesg <"$tmp/log1"
        if [ ${#mesg} -gt 4 ]; then
            mesg="${mesg:0:${#mesg}-4}"
            echo "* Loaded $mesg."
        fi
    done
}

src=$1

    if [ $# -lt 1 ]; then
        echo $0 REPO [TARGET-REPO]
        exit 1
    fi

    while [ "${src: -1}" = "/" ]; do
        src="${src:0:${#src}-1}"
    done

    while [ "${dst: -1}" = "/" ]; do
        dst="${dst:0:${#dst}-1}"
    done

    if [ $# = 1 ]; then
        self=1
        dst=$src.new
    else
        if [ -e "$2" ]; then
            echo "target $2 is already existed. overwrite? (y/n)"
            read force
            if [ "$force" != "y" ]; then
                echo user canceled.
                exit 2
            fi
            rm -fR "$2"
        fi
        dst=$2
    fi

tmp=
stage=
    for oldtmp in "$src".dump.*; do
        echo "found previous dumpped temp: $oldtmp, continue to load?"
        read cont
        if [ "$cont" = y ]; then
            tmp=$oldtmp
            stage=load
            break
        fi
    done
    while [ -z $tmp ]; do
        tmp=$src.dump.$$.$RANDOM
        if [ ! -d "$tmp" ]; then break; fi
    done
    mkdir -p "$tmp"

recreate "$src" "$tmp" "$dst" "$stage"

if [ "$self" = 1 ]; then
    echo "backup src repo $src to $src.bak"
    mv -- "$src" "$src.bak"
    mv -- "$dst" "$src"
fi

echo clean up
if [ $stage != "load" ]; then
    rm -fR "$tmp"
fi

echo recreated, see $dst/loadlog for details.
