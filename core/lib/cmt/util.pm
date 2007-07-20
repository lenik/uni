package cmt::util;

use strict;
use vars            qw/@ISA @EXPORT @EXPORT_OK/;
use cmt::ftime;
use cmt::proxy;
use Data::Dumper;
use Exporter;
use Fcntl;
use Socket qw(:all);
use POSIX;
use YAML;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_strict         = 0;

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

sub forx($&;$) {
    my $exp = shift;
    my $code = shift;
    my $s = shift || $_;
    my $off = 0;
    my $buf;

    untie $_ if my $tieback = tied $_;
    local $_;

    while ($s =~ /$exp/g) {
        $_ = $&;
        $code->();
        # die "Eval fails: $!" if $@;
        $buf .= substr($s, $off, $-[0] - $off) . $_;
        $off = $+[0];
    }

    tie $_, 'cmt::proxy', $tieback if defined $tieback;

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

sub get_named_args(\@;\%) {
    my $arg = shift;
    my $cfg = shift || {};
    my @passby;
    while (@$arg) {
        $_ = shift @$arg;
        if (ref($_) eq '' and /^-(\w+)$/) {
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

sub setnonblock {
    my $h = shift;
    eval {
        info2 "setnonblock $h by Fcntl";
        my $flags = 0;
        fcntl($h, F_GETFL, $flags) or return undef;
        $flags |= O_NONBLOCK;
        fcntl($h, F_SETFL, $flags) or return undef;
        1
    } or eval {
        info2 "setnonblock $h by ioctl";
        my $temp = 1;
        ioctl $h, 0x8004667E, \$temp;
        1
    } or eval {
        info2 "setnonblock $h by setsockopt";
        setsockopt $h, IPPROTO_TCP, TCP_NODELAY, 1;
        1
    } or die "Can't setnonblock, system doesn't support the operation";
}

sub nbread {
    my ($h, $size)  = @_;

    # set non-block
    setnonblock($h) or return undef;

    my $buf;
    my $size1 = defined $size ? $size : 4096;
    my $len = sysread($h, $buf, $size1);

    # May be need this?
    if (defined $len) {
        $buf = '' if $len == 0;
    } else {
        return undef;
    }

    if (! defined $size) {
        my $block;
        while (($len = sysread($h, $block, 4096)) > 0) {
            $buf .= $block;
        }
    }
    return $buf;
}

sub indent {
    my $prefix = shift;
        $prefix = ' 'x$prefix if $prefix =~ /^\d+$/;
    my @lines = split(/\n/, shift);
    join("\n", map { $prefix.$_ } @lines);
}

sub unindent_most {
    my @lines = split(/\n/, shift);
    my ($s) = ($lines[0] =~ /^(\s*)/);
    my $l = length $s;
    join("\n", map { substr($_, $l) } @lines);
}

my  @ATEXIT;
sub atexit(&) {
    my $dstr = shift;
    push @ATEXIT, [$dstr, \@_];
}

# XXX - thread-unsafe ("free unreferenced scalars" in multi-threads.)
END {
    $_->[0]->(@{$_->[1]}) for @ATEXIT;
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

@ISA    = qw(Exporter);
@EXPORT = qw(
	datetime
	cftime
	timestamp10
	forx
	qsplit
	append_cmdline
	get_named_args
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
	setnonblock
	nbread
	indent
	unindent_most
	atexit
	fire_sub
	fire_method
	);

@EXPORT_OK = ();

1;
