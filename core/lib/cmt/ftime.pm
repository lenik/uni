
package cmt::ftime;

use strict;
use vars        qw/@ISA @EXPORT/;
use Exporter;
use Time::HiRes qw/time usleep/;

@ISA    = qw(Exporter);
@EXPORT = qw(ftime
		 fsleep);

# the prototype () is required, to avoid
#       $elaps = ftime - $last_time;
# be parsed as
#       $elaps = ftime(-$last_time);
sub ftime() {
    # my $rhs = shift || 0;
    return time; # + $rhs;
}

sub fsleep {
    my $fsec = shift;
    return if $fsec <= 0;
    my $us = 1000000 * $fsec;
    usleep($us);
}

1;
