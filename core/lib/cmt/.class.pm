package cmt::CLASS_NAME;

use strict;
use vars                qw/@ISA @EXPORT/;
use cmt::util;
use Exporter;

sub info;
sub info2;
sub static_method;

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

# utilities

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

# static methods

sub static_method {
    my (@parameters) = @_;
    # ...
}

1