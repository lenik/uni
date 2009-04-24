#!/bin/bash

ln -nsf $LAM_KALA/xt        /xt
ln -nsf /xt/etc/profile.d   /etc/profile.d/xt
ln -nsf /mnt/d/             /media/cpan

if [ -d /usr/lib/perl5/site_perl/5.10 ]; then
    mv /usr/lib/perl5/site_perl/5.10 /usr/lib/perl5/site_perl/5.10.orig
fi
ln -nsf $LAM_KALA/usr/lib/perl5/site /usr/lib/perl5/site_perl/5.10

ln -nsf $LAM_KALA/etc/cygwin/etc/*       /etc
ln -nsf $LAM_KALA/etc/profile.d          /etc/profile.d/cygwin
