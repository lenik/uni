#!/bin/bash

_chown=1

# applysvn.sh TARGET URL-LIST
if [ $# -lt 2 ]; then
    echo applysvn TARGET URL-LIST
    exit 1
fi

function merge() {
    local destdir="$1"
    local srcsvn="$2"
    local suffix="$3"
    local listfile="$4"

    local first_file=`find "$srcsvn$suffix" \
            -maxdepth 1 \
            -name \#\* -prune \
            -o -type f -print -quit`

    shopt -s dotglob
    shopt -s nullglob

    if [ -n "$first_file" ]; then
        echo  "Found first-file: '$first_file'"

        echo "  Merge ($srcsvn) $suffix -> ($destdir) $suffix"

        if [ ! -d "$destdir$suffix" ]; then
            echo "    Create directory: $destdir$suffix"
            if ! mkdir -p "$destdir$suffix"; then
                echo "    Can't create the directory, exit. "
                exit 1
            fi
        fi

        if [ -n "$listfile" ]; then
            echo "[$suffix]" >>"$listfile"
            echo "src=$suffix" >>"$listfile"
            echo >>"$listfile"
        fi

        if [ -d "$destdir$suffix/.svn" ]; then
            echo "  [INFO] Target is already svn-enabled, skipped"
            exit 0
        fi

        if [ -n "$_chown" ]; then
            echo "    Setup user permission info"
            for f in "$srcsvn$suffix"/*; do
                #echo "      $f"
                name="${f##*/}"
                if [ -f "$destdir$suffix/$name" ]; then
                    if ! chown -vR --reference="$destdir$suffix/$name" "$f"; then
                        echo "      [WARN] Can't set owner of $f"
                    fi
                    if ! chgrp -vR --reference="$destdir$suffix/$name" "$f"; then
                        echo "      [WARN] Can't set group of $f"
                    fi
                    if ! chmod -vR --reference="$destdir$suffix/$name" "$f"; then
                        echo "      [WARN] Can't set file-mode of $f"
                    fi
                fi
            done
        fi

        mv -f "$srcsvn$suffix"/* "$destdir$suffix"

    else

        # Recurse into...
        local subdir subname
        for subdir in "$srcsvn$suffix"/*; do
            subname="${subdir##*/}"
            if [ "$subname" == ".svn" ]; then
                continue
            fi

            merge "$destdir" "$srcsvn" "$suffix/$subname" "$listfile"
        done

    fi

}

TARGET="$1"
    if [ ! -d "$TARGET" ]; then
        echo "Not a directory: $TARGET"
        exit 1
    fi
    shift

    SVNLIST="$TARGET/.applysvn"
    if [ -f "$SVNLIST" ]; then
        mv -f "$SVNLIST" "$SVNLIST.bak"
    fi

for URL in "$@"; do

    echo "Prepare to apply $URL"

    TRANSDIR=/tmp/applysvn-$RANDOM
    svn co "$URL" "$TRANSDIR"

    # find non-empty dir and merge into target dir
    merge "$TARGET" "$TRANSDIR" "" "$SVNLIST"

    echo "Cleanup"
    rm -fR "$TRANSDIR"

done
