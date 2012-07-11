#!/bin/bash -e

dl="http://www.truecrypt.org/download/truecrypt-7.0a-linux-console-x86.tar.gz"
dl_file=`wgetc -q $dl`
single_file=`tar tzf $dl_file`

tar xvf $dl_file $single_file
mv $single_file $1
