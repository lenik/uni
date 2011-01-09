#!/bin/bash

PARENT='HKCU\Software\SimonTatham\PuTTY\Sessions'

reg query "$PARENT" | while read -r k; do

    name="${k##*\\Sessions\\}"
    if [ "$k" = "$name" ]; then continue; fi
    if [ -z "$name" ]; then continue; fi

    echo "$name"
    reg export "$PARENT\\$name" "putty-session-$name.reg" >/dev/null 2>/dev/null \
        || echo "    Failed"

done
