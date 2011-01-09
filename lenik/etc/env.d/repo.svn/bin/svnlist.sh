#!/bin/bash

if [ $# -lt 1 ]; then
    echo "svnlist TARGET-DIR svn-command-arguments..."
    exit 1
fi

TARGET="$1"
    shift
    if [ "${TARGET: -1}" = "/" ]; then
        TARGET="${TARGET:0: ${#TARGET}-1}"
    fi

    SVNLIST="$TARGET/.applysvn"

    if [ -f "$TARGET" ]; then
        SVNLIST="$TARGET"
        TARGET="${SVNLIST%/*}"
    fi

    if [ ! -f "$SVNLIST" ]; then
        echo "List file $SVNLIST isn't existed. "
        exit 1
    fi

echo "Processing $SVNLIST..."
echo

while read LINE; do

    if [ "${LINE:0:1}" != "[" ]; then continue; fi
    if [ "${LINE: -1}" != "]" ]; then continue; fi

    dir="${LINE:1:${#LINE}-2}"
    if [ "${dir:0:1}" != "/" ]; then
        echo "Name isn't normalized(which should be absolute): $dir"
        exit 1
    fi

    echo "Enter $TARGET$dir"

    pushd "$TARGET$dir" >/dev/null
        svn "$@"
    popd >/dev/null

    echo "Leave $TARGET$dir"

    echo
done <"$SVNLIST"
