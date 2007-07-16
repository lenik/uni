package cmt::CLASS_NAME;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::util;
use Exporter;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

@ISA    = qw(Exporter);
@EXPORT = qw(static_method
             );

sub new {
    my $class           = shift;
    my $this            = bless {}, $class;
    $this->{method}     = \&method;
    $this->{attribute}  = undef;
    return $this;
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