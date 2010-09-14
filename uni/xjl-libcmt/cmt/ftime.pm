package cmt::ftime;

use strict;
use Exporter;
use POSIX('strftime');
use Time::HiRes('time', 'usleep');

our @ISA    = qw(Exporter);
our @EXPORT = qw(ftime
		 fsleep
                 cftime
                 localftime
                 );

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

sub cftime {
    my $t = shift || ftime;
    my $ms = $t - int($t);
    my $dot = index($ms, '.');
    $ms = $dot == -1 ? 0 : substr($ms, $dot + 1, 6);
    # my $s = $ms + $t % 60;
    # $t = int($t / 60);
    # my $m = $t % 60;
    # $t = int($t / 60);
    # my $h = $t % 24;
    # return sprintf("%02d:%02d:%02.6f", $h, $m, $s);
    my $secfmt = strftime('%H:%M:%S', gmtime($t));
    return $secfmt . '.' . substr("00000$ms", -6);
}

sub localftime {
    my $t = shift || ftime;
    my $ms = $t - int($t);
    my $dot = index($ms, '.');
       $ms = $dot == -1 ? 0 : substr($ms, $dot + 1, 6);
    # my $s = $ms + $t % 60;
    # $t = int($t / 60);
    # my $m = $t % 60;
    # $t = int($t / 60);
    # my $h = $t % 24;
    # return sprintf("%02d:%02d:%02.6f", $h, $m, $s);
    my $secfmt = strftime('%H:%M:%S', localtime($t));
    return $secfmt . '.' . substr("00000$ms", -6);
}

1