#!/bin/bash

ln -nsf /lapiota/xt     /xt
ln -nsf /mnt/d/         /media/cpan

if [ -d /usr/lib/perl5/site_perl/5.10 ]; then
    mv /usr/lib/perl5/site_perl/5.10 /usr/lib/perl5/site_perl/5.10.orig
fi
ln -nsf /lapiota/usr/lib/perl5/site /usr/lib/perl5/site_perl/5.10
