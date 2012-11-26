#!/bin/bash

HEXTAB=0123456789ABCDEF

for i in {0..63}; do
    [ $((i % 8)) = 0 ] && echo -n "        "
    echo -n "'#"
    for e in {1..3}; do
        c=$(( (RANDOM % 128) + 64 ))
        ch=$(( c / 16 ))
        cl=$(( c % 16 ))
        echo -n ${HEXTAB:ch:1}${hextab:cl:1}
    done
    echo -n "' "
    [ $((i % 8)) = 7 ] && echo
done
