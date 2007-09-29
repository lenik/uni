package cmt::log;

use strict;
use cmt::time('datetime');
# use Data::Dumper;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(_log
                 _sig
                 _sigx
                 );

our $opt_fastlog    = 1;

sub has {
    my ($pkg, $nam) = @_;
    my $vnam = '$'.$pkg.'::'.$nam;
    eval 'defined '.$vnam.' ? \\'.$vnam.' : undef'
}

sub addmissing {
    my ($pkg, $nam, $val) = @_;
    my $vnam = '$'.$pkg.'::'.$nam;
    my $ref = eval '\\'.$vnam;
    $$ref = $val unless defined $$ref;
}

sub import {
    my ($level) = splice @_, 1, 1;
    my ($pkg) = caller(0);

    addmissing $pkg, 'LOGNAME',     'Who?';
    addmissing $pkg, 'LOGLEVEL',    1;
    addmissing $pkg, 'LOGTIME',     0;

    __PACKAGE__->export_to_level(1, @_);

    for (my $i = 0; $i <= $level; $i++) {
        for (qw(_log _sig)) {
            my $s = "sub $pkg\::$_$i { \$$pkg\::LOGLEVEL >= $i && &$_ }; 1\n";
            eval $s or die "can't import level $i: $@";
        }
    }
}

sub _findopt {
    my $nam = shift;
    my $c = 1 + shift;
    while (1) {
        my ($pkg) = caller($c++) or last;
        my $ref = has($pkg, $nam);
        return $ref if ref $ref;
    }
    undef # not find in packages in call stack.
}

# vec cache by call-stack
# TODO: now using caller(1).package as hash of call-stack
#       for common scenario, there are not too many log providers,
#       (typically 2 at most), thus whatever call-stack is,
#       the results of _findopt are same.
my %_CS_VEC;
sub _vec {
    my ($pkg) = caller(1);
    my $vec = $_CS_VEC{$pkg};
    unless (defined $vec) {
        my $l   = _findopt('LOGLEVEL',  1);
        my $n   = _findopt('LOGNAME',   1);
        my $t   = _findopt('LOGTIME',   1);
        $vec = [ $l, $n, $t ];
        $_CS_VEC{$pkg} = $vec if $opt_fastlog;
    }
    $vec
}

sub _log {
    my $V = _vec; my (undef, $n, $t) = @$V;
    print STDERR datetime.' ' if $$t;
    print STDERR "[$$n] ", @_, "\n";
}

sub _sig {
    my $cls = shift; local $_ = join('', @_);
    return unless -t STDERR or s/\n$//s;
    printf STDERR "[%4s] %-72s".(-t STDERR ? "\r" : "\n"), $cls, $_;
}

sub _sigx {
    print STDERR (-t STDERR ? '' : "\n"), '    err: ', @_, "\n";
}

1