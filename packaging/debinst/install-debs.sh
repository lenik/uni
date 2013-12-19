#!/bin/bash

_dir_="${0%/*}"
if [ "$_dir_" = "$0" ]; then
    _dir_=.
fi

cd "$_dir_"

dpkg "$@" -i *.deb

apt-get install -f
