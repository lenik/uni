#!/bin/sh

if [ $# != 3 ]; then
	echo "$0 srcdir sysconfdir sudoersdir"
	exit 1
fi

srcdir="$1"
sysconfdir="$2"
sudoersdir="$3"

# IMPORTANT.
chmod 440 $sudoersdir/dev

$srcdir/check-uid.sh $sysconfdir 70 99 700 999
$srcdir/check-gid.sh $sysconfdir 70 99 700 999
$srcdir/70.sh  $sysconfdir
$srcdir/700.sh $sysconfdir

setminuid 800

exit 0
