
use strict;
use cmt::util;
use Data::Dumper;

my $cmdline = 'mtpulse C O1 X2 O3 X4 O5 X6 O7 X8 O9 X10';
print "pre [";
system $cmdline;
print "]\n";

my @ret = io_cap {
    system $cmdline;
    };

my $onlyout = io_cap {
    system $cmdline;
    };

print "onlyout = [".$onlyout."]\n";

print Dumper(\@ret);
