package cmt::stream;

use strict;
use vars                qw/@ISA @EXPORT/;
use cmt::util;
use Data::Dumper;
use Exporter;

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
    my ($in, $out)      = @_;
    $this->{IN}         = $in   if defined $in;
    $this->{OUT}        = $out  if defined $out;
    # info2 Dumper(\@_);
    # info2 'new cmt::stream = '.Dumper($this);
    return $this;
}

sub fire {
    my $this            = shift;
    cmt::util::fire $this, @_;
}

sub bind {
    my $this            = shift;
    my ($in, $out)      = @_;
        $out ||= $in;
    $this->{IN}         = $in;
    $this->{OUT}        = $out;
    $this->fire('binded');
}

sub unbind {
    my $this            = shift;
    undef $this->{IN};
    undef $this->{OUT};
    $this->fire('unbinded');
}

sub push {
    my $this    = shift;
    $this->fire('gotdata', @_);
}

sub pull {
    my $this    = shift;
    $this->fire('askdata', @_);
}

sub err {
    my $this    = shift;
    $this->fire('goterr', @_);
}

# non-block read
sub read {
    my $this    = shift;
    my $fd      = shift || $this->{IN};
    return nbread($fd);
}

sub write {
    my $this    = shift;
    my $fd      = $this->{OUT};
    print $fd join('', @_);
}

sub autoflush {
    my $this    = shift;
    my $fd      = $this->{OUT};
    my $val     = shift || 1;
    $fd->autoflush($val),
}

sub close {
    my $this            = shift;
    my $IN              = $this->{IN};
    my $OUT             = $this->{OUT};
    undef $OUT          if $OUT == $IN;
    close $IN           if defined $IN;
    close $OUT          if defined $OUT;
    $this->unbind;
}

sub shutdown {
    my $this            = shift;
    my $m               = shift || 2;
    my $IN              = $this->{IN};
    my $OUT             = $this->{OUT};
    undef $OUT          if $OUT == $IN;
    shutdown($IN, $m)   if defined $IN;
    shutdown($OUT, $m)  if defined $OUT;
    $this->unbind;
}

1