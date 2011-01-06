#!/bin/bash

. shlib-import cliboot

mkdir -p lib

libdir=`readlink -f lib`

cd /mnt/istore/m2/org/apache/derby/derby/ || die "istore/m2/.../derby doesn't exist. "

latest=
for d in */; do latest="${d%/}"; done

cp -vf $latest/derby-$latest.jar "$libdir/" || die "Failed to copy"
ln -vsnf derby-$latest.jar "$libdir/derby.jar"
