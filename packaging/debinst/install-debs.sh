#!/bin/bash

_dir_="${0%/*}"
if [ "$_dir_" = "$0" ]; then
    _dir_=.
fi

cd "$_dir_"

if [ -x ./pre-inst ]; then
    ./pre-inst
fi

dpkg "$@" -i *.deb

apt-get install -f

if [ -x ./post-inst ]; then
    ./post-inst
fi

