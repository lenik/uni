#!/bin/bash

ln -nsf /lapiota/xt         /xt
ln -nsf /xt/etc/profile.d   /etc/profile.d/xt
ln -nsf /mnt/d/             /media/cpan

if [ -d /usr/lib/perl5/site_perl/5.10 ]; then
    mv /usr/lib/perl5/site_perl/5.10 /usr/lib/perl5/site_perl/5.10.orig
fi
ln -nsf /lapiota/usr/lib/perl5/site /usr/lib/perl5/site_perl/5.10

ln -nsf /lapiota/etc/cygwin/etc/*       /etc
ln -nsf /lapiota/etc/profile.d          /etc/profile.d/cygwin
