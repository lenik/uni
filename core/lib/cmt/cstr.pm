package cmt::cstr;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::util;
use Data::Dumper;
use Exporter;
use Tie::Scalar;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

@ISA    = qw(Exporter Tie::StdScalar);
@EXPORT = qw(cs_comp
             cs_alias
             );

# tie $scalar, cmt::cstr[, $join, $split]
sub TIESCALAR {
    my ($class, $join, $split) = splice(@_, 0, 3);
    my $inst = {
        'join'      => $join || sub { join(' ', @_) },
        'split'     => $split || sub { split(/\s+/, shift, shift) },
        'C'         => $#_ ? \@_ : $_[0],
    };
    bless $inst, $class;
}

sub FETCH {
    my $this    = shift;
    my $join    = $this->{join};
    my $C       = $this->{C};
    $join->(map { $$_ } @$C);
}

sub STORE {
    my $this    = shift;
    my $value   = shift;
    my $split   = $this->{split};
    my $C       = $this->{C};
    my @values  = $split->($value, scalar @$C);
    for my $i (0..$#$C) {
        ${$C->[$i]} = $values[$i];
    }
}

# static methods

# cs_comp $c => [ \$a, \$b ], -join=>sub {}, -split=>sub {}
sub cs_comp(\$$;%) {
    my $ref     = shift;
    my $C       = shift;
    my $join;
    my $split;
    if ($#_) {
        my %opt = @_;
        $join   = $opt{-join};
        $split  = $opt{-split};
    } else {
        my $sep = shift;
        $join   = sub { join($sep, @_) };
        $split  = sub { split($sep, shift, shift) };
    }
    tie $$ref, __PACKAGE__, $join, $split, $C;
}

sub cs_alias(\$\$) {
    my ($cs, $s) = @_;
    cs_comp $$cs => [ $s ],
            -join => sub { shift },
            -split => sub { shift };
}

# cs_XXX, ...

1