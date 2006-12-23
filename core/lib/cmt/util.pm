package cmt::util;

use strict;
use POSIX;
use Exporter;
use cmt::ftime;
use vars qw/@ISA @EXPORT/;

# -> cdatetime
sub datetime {
    return strftime('%Y-%m-%d %H:%M:%S', localtime);
}

sub cftime {
    my $t = shift || ftime;
    my $ms = $t - int($t);
    my $dot = index($ms, '.');
    my $ms = $dot == -1 ? 0 : substr($ms, $dot + 1, 6);
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
    my $ms = $dot == -1 ? 0 : substr($ms, $dot + 1, 6);
    # my $s = $ms + $t % 60;
    # $t = int($t / 60);
    # my $m = $t % 60;
    # $t = int($t / 60);
    # my $h = $t % 24;
    # return sprintf("%02d:%02d:%02.6f", $h, $m, $s);
    my $secfmt = strftime('%H:%M:%S', localtime($t));
    return $secfmt . '.' . substr("00000$ms", -6);
}

sub timestamp10 {
    my @now     = localtime;
    my $t       = shift || \@now;
    my $sep     = shift || '';
    my $sep1    = shift || '';
    my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)
                = @$t;
    my $YY      = substr($year, -2);
    my $DDD     = substr("000$yday", -3);
    my $SSSSS   = substr('00000'. (($hour * 24 + $min) * 60 + $sec), -5);
    $YY . $sep1 . $DDD . $sep . $SSSSS;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	datetime
	cftime
	timestamp10
	);

1;
