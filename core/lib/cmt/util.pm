package cmt::util;

use strict;
use vars                qw/@ISA @EXPORT @EXPORT_OK/;
use cmt::ftime;
use cmt::proxy;
# use Data::Dumper;
use Exporter;
use POSIX               qw/strftime/;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_strict         = 0;

sub datetime;

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

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

my %QRMODE = (
    'c'         => qr/[()\[\]{}?*+.|^\$\\]/,
    'o'         => qr/[()\[\]{}?*+.^\$\\]/,
);
sub qr_literal {
    my $text    = shift;
    my $mode    = shift || 'c';
       $mode    = $QRMODE{$mode} or die "Invalid mode $mode";
    $text       =~ s/$mode/\\$&/g;
    return qr/$text/;
}

sub forx($&;$$) {
    my $exp     = shift;
    my $hit     = shift;
    my $miss;
    my $s       = $_[0];
    ($miss, $s) = @_ if ref $s eq 'CODE';

    my $off = 0;
    my $buf;

    untie $_ if my $tieback = tied $_;
    local $_;

    while ($s =~ /$exp/g) {
        my $m = $&;

        $_ = substr($s, $off, $-[0] - $off);
        if ($_ ne '') {
            $miss->() if defined $miss;
            $buf .= $_;
        }

        $_ = $m;
        $hit->() if defined $hit;
        $buf .= $_;

        $off = $+[0];
    }
    $_ = substr($s, $off);
    if ($_ ne '') {
        $miss->() if defined $miss;
        $buf .= $_;
    }

    tie $_, 'cmt::proxy', $tieback if defined $tieback;
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

sub get_named_args(\@;\%) {
    my $arg = shift;
    my $cfg = shift || {};
    my @passby;
    while (@$arg) {
        $_ = shift @$arg;
        if (ref($_) eq '' and /^(-\w+)$/) {
            $cfg->{$1} = shift @$arg;
        } else {
            push @passby, $_;
        }
    }
    @$arg = @passby;
    return %$cfg;
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

sub array_index(\@$@) {
    sub _eq { $_[0] == $_[1] }
    my ($array, $v, $cmp) = @_;
    $cmp = \&_eq unless defined $cmp;
    for (0..$#$array) {
        return $_ if $cmp->($array->[$_], $v);
    }
    return -1;
}

sub array_remove(\@$@) {
    my ($array, $v, $cmp) = @_;
    my $i = array_index(@$array, $v, $cmp);
    return undef if $i < 0;
    splice @$array, $i, 1;
}

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
    my @lines;
    undef $!;
    if ($path =~ /\.gz$/i) {
        eval('use Compress::Zlib; 1')
            or die("Can't load Compress::Zlib: $@");
        open(FH, "<$path")
            or die("Can't open file $path for read");
        my $h = gzopen(\*FH, "rt")
            or die("Can't open gzip-file $path for read");
        while ($h->gzreadline($_)) {
            push @lines, $_;
        }
        # return 'string' when success, otherwise an error-code
        my $err = eval('$gzerrno');
        if ($err != eval('Z_STREAM_END')) {
            $! = "Error deflating from $path: $err\n";
        }
        $h->gzclose();
        close FH;
    } else {
        open(FH, "<$path")
            or die("Can't open file $path for read");
        @lines = <FH>;
        close FH;
    }
    return wantarray ? @lines : join('', @lines);
}

sub writefile {
    my $path = shift;
    open(FH, ">$path")
        or die("Can't open file $path to write");
    print FH for @_;
    close FH;
}

sub indent {
    my $prefix = shift;
        $prefix = ' 'x$prefix if $prefix =~ /^\d+$/;
    my @lines = split(/\n/, shift);
    join("\n", map { $prefix.$_ } @lines);
}

sub unindent_ {
    my $len     = shift;
    my @lines   = scalar @_ > 1 ? @_ : split(/\n/, shift);
    if ($len <= 0) {
        my ($s) = ($lines[0] =~ /^(\s*)/);
        $len    = length $s;
    }
    my $pattern = qr/^\s{1,$len}/;
    join("\n", map { s/$pattern//; $_ } @lines);
}

sub unindent {
    unindent_ 0, @_
}

sub abbrev {
    my $maxlen = shift;
    my $text = join(@_);
    $text =~ s/\n/ /g;
    if (length $text > $maxlen) {
        substr($text, $maxlen - 5) = '...';
    }
    return $text;
}

sub fire_sub {
    my $obj = shift;
    my $name = shift;
    if (defined (my $callback = $obj->{$name})) {
        if (ref $callback eq 'CODE') {
            return $callback->(@_);
        } elsif (ref $callback eq '') {     # string
            return eval($callback);
        } else {
            die "$obj->\{$name\}: invalid callback value: $callback";
        }
    } else {
        if ($opt_strict) {
            die "callback $name isn't existed in $obj.";
        }
        return undef;
    }
}

sub fire_method {
    my $obj = shift;
    my $name = shift;
    if ($obj->can($name)) {
        return $obj->$name(@_);
    }
    return fire_sub($obj, $name, $obj, @_);
}

my  @ATEXIT;
sub at_exit(&) {
    my $dstr = shift;
    push @ATEXIT, [$dstr, \@_];
}

# XXX - thread-unsafe ("free unreferenced scalars" in multi-threads.)
END {
    $_->[0]->(@{$_->[1]}) for @ATEXIT;
}

@ISA    = qw(Exporter);
@EXPORT = qw(
	datetime
	cftime
	timestamp10
	qr_literal
	forx
	qsplit
	append_cmdline
	get_named_args
	arraycmp
	arrayeq
	arrayne
	array_index
	array_remove
	hasheq
	hashne
	hashindex
	hash2tuples
	bserchi
	readfile
	writefile
	indent
	unindent_
	unindent
	abbrev
	fire_sub
	fire_method
	at_exit
	);

@EXPORT_OK = ();

1;
