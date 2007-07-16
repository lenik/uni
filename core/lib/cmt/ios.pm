package cmt::ioevt;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::util;
use Exporter;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

@ISA    = qw(Exporter IO::Select);
@EXPORT = qw(static_method
             );

sub new {
    my $class = shift;
    bless new IO::Select(@_), $class;
}

sub method {
    my $this = shift;
    # ...
}

# static methods

sub static_method {
    my (@parameters) = @_;
    # ...
}

1