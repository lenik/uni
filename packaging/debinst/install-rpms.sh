#!/bin/bash

_dir_="${0%/*}"
if [ "$_dir_" = "$0" ]; then
    _dir_=.
fi

cd "$_dir_"

for rpm in *.rpm; do
    base="${rpm%.*}"        # no extension
    base="${base%.*}"       # no arch
    base="${base%-*}"       # no debian ver

    echo "Install $base"

    base="${base%-*}"       # no ver

    # Remove anyway
    rpm -e "$base" >/dev/null 2>/dev/null
    
    # Re-install it.
    rpm "$@" -i --nodeps --force $rpm

    echo
done

