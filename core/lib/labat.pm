package labat;          # Lapiota Batch-Processing

use strict;
use cmt::util;
use Exporter;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(labat_compile
                 );

sub labat_compile {
    my $root = shift;
    my $cat = {};
    my $ctx = {};
}

sub _compile {
    my ($cat, $fun, $node);
    if ($fun =~ /^:(\S+)$/) {
        my $cat_name = $1;
        my $subcat = $cat->{$cat_name} = {};
        _compile($subcat,
        # :: description node should be filtered out.
    } else {
        die "unknown fun-name: $fun" unless defined $funs->{$fun};
        $fun_proto = $funs->{$fun};
        $fun_proto->($node);
    }

sub _stdfun_proto {
    my ($fun_name, $arg_node, $funs, $vars) = @_;
    my $code =
    die "fun $fun_name isn't existed" unless ref $funs->{$fun_name};
    $fun_name
    my @calls_with = _arg_unzip($arg_node);
    return undef unless @calls_with;
    my @calls;
    push @calls, 'do-seq' if @calls_with > 1;
    for (@calls_with) {
        push @calls, [ $fun, @$_ ];
    }
}

sub _arg_unzip {
    my $node = shift;
    return $node unless (ref $node eq 'ARRAY');
    my @list;
    my @prefix;
    local $_;
    for (my $i = 0; $i < @$node; $i++) {
        $_ = $node->[$i];
        if (ref $_ eq 'ARRAY') {
            if (@prefix) {
                my @sublist = _arg_unzip($_);
                for (@sublist) {
                    push @list, [ @prefix, $_ ];
                }
                undef @prefix;
            } else {
                push @list, [ $_ ];
            }
        } elsif (not ref $_ and s/->$//) {
            push @prefix, $_;
        } else {
            # non-ARRAY-ref or scalar
            if (@prefix) {
                push @list, [ @prefix, $_ ];
                undef @prefix;
            } else {
                push @list, $_;
            }
        }
    }
    return @list;
}

1