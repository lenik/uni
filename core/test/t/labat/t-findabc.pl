#!env perl

use strict;
use labat::lapiota;

for my $name qw(auto eclipse maven msys) {
    print "first $name: " . findabc($name) . "\n";
    print "last $name: " . findabc('-z', $name) . "\n";
    print "last $name in win32: " . findabc('-z', '-w', $name) . "\n";

    print "$name list: ";
        my @list = findabc('-l', $name);
        print "  $_\n" for @list;

    print "\n";
}
