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
    my $self            = bless {}, $class;
    $self->{method}     = \&method;
    $self->{attribute}  = undef;
    return $self;
}

sub method {
    my $self = shift;
    # ...
}

# static methods

sub static_method {
    my (@parameters) = @_;
    # ...
}

1