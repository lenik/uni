package cmt::channel;

use strict;
use IO::Socket;
use IO::Select;

sub new {
    my $class = shift;
    my $self = bless {}, $class;
    $self->{initf}  = \&init;
    $self->{uninitf}= \&uninit;
    $self->{recvf}  = \&recv;
    $self->{sentf}  = \&sent;
    $self->{idlef}  = \&idle;
    $self->{serv}   = undef;
    $self->{IN}     = \*STDIN;
    $self->{OUT}    = \*STDOUT;
    return $self;
}

sub _bind_ {
    my $self = shift;
    my ($serv, $in, $out) = @_;
    $out ||= $in;
    $self->{serv}       = $serv;
    $self->{IN}         = $in;
    $self->{OUT}        = $out;
    $self->fire_init;
}

sub serv        { return shift->{serv}; }
sub IN          { return shift->{IN}; }
sub OUT         { return shift->{OUT}; }

sub init {
    my $self = shift;
    # do nothing.
}

sub uninit {
    my ($self) = @_;
    # ignore this event
}

sub recv {
    my ($self, $msg) = @_;
    # ignore this event
}

sub sent {
    my ($self) = @_;
    # ignore this event
}

sub idle {
    my ($self) = @_;
    # ignore this event
}

sub fire_init {
    my $self = shift;
    my $f = $self->{initf};
    return $f->($self, @_);
}

sub fire_uninit {
    my $self = shift;
    my $f = $self->{uninitf};
    return $f->($self, @_);
}

sub fire_recv {
    my $self = shift;
    my $f = $self->{recvf};
    return $f->($self, @_);
}

sub fire_sent {
    my $self = shift;
    my $f = $self->{sentf};
    return $f->($self, @_);
}

sub fire_idle {
    my $self = shift;
    my $f = $self->{idlef};
    return $f->($self, @_);
}

sub send {
    my $OUT = shift->OUT;
    my ($buf) = @_;
    print $OUT $buf;
}

sub close {
    my $self = shift;
    my $IN = $self->IN;
    my $OUT = $self->OUT;
    $OUT = undef if $OUT == $IN;

    # my $select = $self->serv->select;
    # $select->remove $IN if defined $IN;
    # $select->remove $OUT if defined $OUT;
    close $IN if defined $IN;
    close $OUT if defined $OUT;
}

sub shutdown {
    my $self = shift;
    my $IN = $self->IN;
    my $OUT = $self->OUT;
    $OUT = undef if $OUT == $IN;

    # my $select = $self->serv->select;
    # $select->remove $IN if defined $IN;
    # $select->remove $OUT if defined $OUT;
    shutdown($IN, 2) if defined $IN;
    shutdown($OUT, 2) if defined $OUT;
}


# static methods

use Exporter;
use vars qw/@ISA @EXPORT/;

sub mkch {
    my ($recvf, $idlef, $initf, $uninitf) = @_;
    my $ch = new cmt::channel;
    $ch->{initf}    = $initf if ($initf);
    $ch->{uninitf}  = $uninitf if ($uninitf);
    $ch->{recvf}    = $recvf if ($recvf);
    $ch->{idlef}    = $idlef if ($idlef);
    return $ch;
}

sub mkchprov {
    sub { mkch @_ }
}

@ISA = qw(Exporter);
@EXPORT = qw(
    mkch
	mkchprov
	);

1;
