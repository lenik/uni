#!/bin/sh

if [ $# != 3 ]; then
	echo "$0 srcdir sysconfdir sudoersdir"
	exit 1
fi

srcdir="$1"
sysconfdir="$2"
sudoersdir="$3"

$srcdir/remove-uid.sh $sysconfdir 70 99 700 999
$srcdir/remove-gid.sh $sysconfdir 70 99 700 999

exit 0
