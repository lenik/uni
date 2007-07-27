package cmt::stream;

use strict;
use vars                qw/@ISA @EXPORT/;
use cmt::netutil;
use cmt::util;
use Data::Dumper;
use Exporter;
use Socket;

sub info;
sub info2;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 2;

@ISA    = qw(Exporter);
@EXPORT = qw();

sub new {
    my $class           = shift;
    my $this            = bless {}, $class;
    get_named_args(@_, %$this);
    my $fh              = shift;
    $this->{'IO'}       = $fh;
    # info2 Dumper(\@_);
    # info2 'new cmt::stream = '.Dumper($this);
    return $this;
}

sub fire {
    my $this            = shift;
    fire_method $this, @_;
}

sub bind {
    my $this            = shift;
    my $fh              = shift;
    $this->{'IO'}       = $fh;
    $this->fire('-binded', $fh);
}

sub unbind {
    my $this            = shift;
    my $fh              = $this->{'IO'};
    undef $this->{'IO'};
    $this->fire('-unbinded', $fh);
}

sub push {
    my $this    = shift;
    $this->fire('-gotdata', @_);
}

sub pull {
    my $this    = shift;
    $this->fire('-askdata', @_);
}

sub err {
    my $this    = shift;
    $this->fire('-goterr', @_);
}

# non-block read
sub read {
    my $this    = shift;
    my $fh      = shift || $this->{IO};
    return nbread($fh);
}

sub write {
    my $this    = shift;
    my $data    = shift;
    my $fh      = shift || $this->{IO};
    print $fh $data;
}

sub autoflush {
    my $this    = shift;
    my $val     = shift || 1;
    my $fh      = shift || $this->{IO};
    $fh->autoflush($val),
}

sub close {
    my $this            = shift;
    $this->forh(sub {
        my ($h, $name)  = @_;
        close $h;
        # undef $this->{$name};
    });
    # $this->unbind;
}

sub shutdown {
    my $this            = shift;
    my $m               = shift || 2;
    $this->forh(sub {
        my ($h, $name)  = @_;
        shutdown $h, 2;
        # undef $this->{$name};
    });
    # $this->unbind;
}

# utilities

sub forh {
    my $this = shift;
    my $code = shift;
    for (keys %$this) {
        my $val = $this->{$_};
        if (UNIVERSAL::isa($val, 'GLOB')) {
            $code->($val, $_);
        }
    }
}

sub hinfo {
    my $this = shift;
    my $info = $this->{info};
    unless (defined $info) {
        $this->forh(sub { $info .= sockinfo(@_) . ' ' });
        chop $info;
        $this->{info} = $info;
    }
    return $info;
}

1