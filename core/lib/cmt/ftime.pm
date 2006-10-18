
package cmt::ftime;

use strict;
use Time::HiRes qw/time usleep/;
use Exporter;
use vars qw/@ISA @EXPORT/;

# the rhs is fixed to the common error:
# when used in
#       $elaps = ftime - $last_time;
# will be parsed as
#       $elaps = ftime(-$last_time);
sub ftime {
    my $rhs = shift || 0;
    return time + $rhs;
}

sub fsleep {
    my $fsec = shift;
    my $us = 1000000 * $fsec;
    usleep($us);
}

@ISA = qw(Exporter);
@EXPORT = qw(
	ftime
	fsleep
	);

1;
