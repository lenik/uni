package cmt::util;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::ftime;
use Data::Dumper;
use Exporter;
use POSIX;
use YAML;

# -> cdatetime
sub datetime {
    return strftime('%Y-%m-%d %H:%M:%S', localtime);
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

sub forx($&;$) {
    my $exp = shift;
    my $code = shift;
    my $s = shift || $_;
    my $off = 0;
    my $buf;
    local $_;
    while ($s =~ /$exp/g) {
        $_ = $&;
        $code->();
        # die "Eval fails: $!" if $@;
        $buf .= substr($s, $off, $-[0] - $off) . $_;
        $off = $+[0];
    }
    $buf .= substr($s, $off);
    return $buf;
}

sub qsplit {
    my $sep = shift;
    my $s = shift || $_;
    $s =~ s/\\/\\\\/g;
    $s =~ s/>/\\>/g;
    my @mem;
    my $k = 0;
            # qr/(["']) (\\\\.|[^\1])* \1/x, $s;
    $s = forx qr{ (" (\\\\.|[^"])* ")
                 |(' (\\\\.|[^'])* ')}x,
              sub { push @mem, $_; $_ = '<'.$k++.'>' },
              $s;
    map {
            s/<(\d+)>/$mem[$1]/g;
            s/\\>/>/g;
            s/\\\\/\\/g;
            $_
        }
        split($sep, $s);
}

sub append_cmdline {
    my $cmdline = shift;
    push @ARGV, qsplit(qr/\s+/, $cmdline);
}

sub arraycmp {
    my ($a, $b) = @_;
    my $c = $#$a - $#$b;
    return $c if $c != 0;
    for (0..$#$a) {
        $c = $a->[$_] cmp $b->[$_];
        return $c if $c != 0;
    }
    return 0;
}

sub arrayeq     { arraycmp(@_) == 0 }
sub arrayne     { arraycmp(@_) != 0 }

sub hasheq {
    my ($a, $b) = @_;
    my $na = scalar(keys %$a);
    my $nb = scalar(keys %$b);
    return 0 if $na != $nb;
    for (keys %$a) {
        return 0 if $a->{$_} ne $b->{$_};
    }
    return 1;
}

sub hashne      { ! hasheq(@_) }

sub hashindex {
    my ($hash, $val) = @_;
    for (keys %$hash) {
        return $_ if $hash->{$_} == $val;
    }
    undef;
}

sub hash2tuples {
    my $hashref = shift;
    my @tuples;
    for my $k (keys %$hashref) {
        push @tuples, [$k => $hashref->{$k}];
    }
    return \@tuples;
}

# to avoid conflict with POSIX::bsearch
sub bserchi(&$@) {
    my $cmp = shift || sub { $a cmp $b };
    my $x = shift;
    my ($l, $r) = (0, scalar @_);
    while ($l < $r) {
        my $m = $l + int(($r - $l) / 2);
        my $t = $_[$m];
        if ($t < $x) {
            $l = $m + 1;
        } elsif ($x < $t) {
            $r = $m;
        } else {
            # $t == $x
            return $m;
        }
    }
    return $l;
}

sub readfile {
    my $path = shift;
    open(FH, "<$path")
        or die("Can't open file $path for read");
    my @lines = <FH>;
    close FH;
    return wantarray ? @lines : join('', @lines);
}

sub writefile {
    my $path = shift;
    open(FH, ">$path")
        or die("Can't open file $path to write");
    print FH for @_;
    close FH;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	datetime
	cftime
	timestamp10
	forx
	qsplit
	append_cmdline
	arraycmp
	arrayeq
	arrayne
	hasheq
	hashne
	hashindex
	hash2tuples
	bserchi
	readfile
	writefile
	);

1;
