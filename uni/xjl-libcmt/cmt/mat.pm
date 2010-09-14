package cmt::mat;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub new_space {
    my (@dims) = @_;
    return [\@dims, [], ];
}

sub adds {
    my ($a, $b) = @_;
    for my $i (0..$#{$a}) {
        $a->[$i] += $b->[$i];
    }
}

sub subs {
    my ($a, $b) = @_;
    for my $i (0..$#{$a}) {
        $a->[$i] -= $b->[$i];
    }
}

sub dots {
    my ($a, $b) = @_;
    for my $i (0..$#{$a}) {
        $a->[$i] *= $b->[$i];
    }
}

sub rands {
    my @rands;
    push @rands, int($_ * rand) for @_;
    return @rands;
}

@ISA = qw(Exporter);
@EXPORT = qw(
             adds
             subs
             dots
             rands
             );

1;
