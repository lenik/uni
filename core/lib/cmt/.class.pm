package cmt::CLASS_NAME;

use strict;
use vars qw/@ISA @EXPORT/;
use Exporter;

sub new {
    my $class   = shift;
    my $self    = bless {}, $class;
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

@ISA = qw(Exporter);
@EXPORT = qw(
	static_method
	);

1;
