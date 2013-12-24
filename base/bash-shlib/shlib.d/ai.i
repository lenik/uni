# -*- mode: sh -*-
# vim: set filetype=sh :

: ${aikf:=README}

function load-image() {
    local caller="${BASH_SOURCE[1]}"
    if [ "${caller:0:5}" = '/dev/' ]; then
        if [ -f "$AI_SOURCE" ]; then
            caller="$AI_SOURCE"
        fi
    fi

    caller="${caller%.in}"
    caller="${caller%.ain}"

    local dir="${caller%/*}"
    local base="${caller##*/}"
    if [ "$dir" = "$caller" ]; then
        dir=.
        base="$caller"
    fi

    if [ -f "$dir/$base.i" ]; then
        . "$dir/$base.i"
        return
    fi

    local img
    local found=0
    local pkg=aiai
    if [ -n "$caller_pkg" ]; then
        pkg="$caller_pkg"
    fi

    for img in \
            "$dir/$base.img" \
            "$dir/.$base.img" \
            "/usr/share/$pkg/$base.img" \
            "/usr/share/$pkg/image/$base.img"; do
        if [ -f "$img" ]; then
            found=1
            break
        fi
    done

    [ $found = 0 ] && return 1

    .. "$img"
}

: ${caller_pkg:=$PACKAGE}
: ${AI_KEYFILE:=$docdir/$caller_pkg/$aikf}

if [ -s "${AI_KEYFILE}" ]; then
    export AI_KEYFILE
else
    unset AI_KEYFILE
fi
