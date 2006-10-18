
# simple tcp servers

package cmt::simpled;

use strict;
use cmt::serv;
use cmt::channel;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub echod_recv {
    my ($self, $msg) = @_;
    $self->send($msg);
}
sub echod {
    my $port = shift || 7;
    return new cmt::serv(mkchprov(\&echod_recv),
        $port, 'echod');
}

sub discard_recv {
    my $self = shift;
}
sub discard {
    my $port = shift || 9;
    return new cmt::serv(mkchprov(\&discard_recv),
        $port, 'discard');
}

sub timed_proc {
    my $self = shift;
    my $str = gmtime;
    $self->send($str);
}
sub timed_idle {
    shift->shutdown;
}
sub timed {
    my $port = shift || 13;
    return new cmt::serv(mkchprov(undef, \&timed_idle, \&timed_proc),
        $port, 'timed');
}

my @QOTD = (
    'qotd1',
    'qotd2',
    );
sub qotd_proc {
    my $self = shift;
    my $count = scalar(@QOTD);
    my $index = int($count * rand);
    my $str = $QOTD[$index];
    $self->send($str);
}
sub qotd {
    my $port = shift || 17;
    return new cmt::serv(mkchprov(undef, undef, \&qotd_proc),
        $port, 'qotd');
}

sub chargend_idle {
    my $self = shift;
    $self->send("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ\n");
}
sub chargend {
    my $port = shift || 19;
    my $serv = new cmt::serv(mkchprov(undef, \&chargend_idle),
        $port, 'chargend');
    $serv->{interval} = 0.01;
    return $serv;
}

sub simpled {
    my @servs = @_;
    if (! @servs) {
        @servs = ( echod, discard, timed, qotd, chargend, );
    }
    my %stats;
    my @pids;
    for my $serv (@servs) {
        my $pid = fork;
        if ($pid) {
            push @pids, $pid;
        } else {
            my $name = $serv->name;
            my $stat = $serv->serv;
            $stats{$name} = $stat;      # this must using IPC.
        }
    }
    return \@pids;
}

@ISA = qw(Exporter);
@EXPORT = qw(
    echod
    discard
    timed
    qotd
	chargend
	simpled
	);

1;
