package cmt::time;

use strict;
use Exporter;
use POSIX('strftime');

our @ISA    = qw(Exporter);
our @EXPORT = qw(datetime
                 timestamp10
                 );

# -> cdatetime
sub datetime {
    return strftime('%Y-%m-%d %H:%M:%S', localtime);
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

1