package cmt::lang;

use strict;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(_a
                 _def
                 _or
                 );

# de-alias
sub _a          { shift }

sub _def(\$$)   { my $v = shift; $$v = shift unless defined $$v }

sub _or         { for (@_) { return $_ if defined $_ } undef }

1