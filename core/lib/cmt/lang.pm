package cmt::lang;

use strict;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(_a
                 _emptyp _zerop
                 _or _o _NA _def
                 _sor _so _sNA _sdef
                 );

# de-alias
sub _a          { shift }

sub _emptyp     { my $v = shift; !defined($v) || ($v eq '') }
sub _zerop      { my $v = shift; !defined($v) || ($v == 0) }

sub _or         { for (@_) { return $_ if defined $_ } undef }
sub _o          { for (@_) { return $_ if defined $_ } '' }
sub _NA         { for (@_) { return $_ if defined $_ } 'N/A' }
sub _def(\$@)   { my $v = shift; $$v = _or($$v, @_) }

sub _sor        { for (@_) { return $_ unless _emptyp $_ } undef }
sub _so         { for (@_) { return $_ unless _emptyp $_ } '' }
sub _sNA        { for (@_) { return $_ unless _emptyp $_ } 'N/A' }
sub _sdef(\$@)  { my $v = shift; $$v = _sor($$v, @_) }

1