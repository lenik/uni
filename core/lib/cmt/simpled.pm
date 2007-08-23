
# simple tcp servers

package cmt::simpled;

use strict;
use cmt::serv;
use cmt::stream;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub echod {
    my $port = shift || 7;
    return new cmt::serv(
        sub {
            new cmt::stream(
                -gotdata => sub {
                    my ($this, $msg) = @_;
                    $this->write($msg);      # ...
                },
            )
        },
        -port => $port,
        -name => 'echod');
}

sub discard {
    my $port = shift || 9;
    return new cmt::serv(
        sub {
            new cmt::stream(
                -gotdata => sub {
                    1
                },
            )
        },
        -port => $port,
        -name => 'discard');
}

sub timed {
    my $port = shift || 13;
    return new cmt::serv(
        sub {
            new cmt::stream(
                -binded => sub {
                    my $s = shift;
                    my $str = gmtime;
                    $s->write($str);
                },
                -sent => sub {              # ???
                    my $s = shift;
                    $s->shutdown(2);
                    $s->unbind;
                },
            )
        },
        -port => $port,
        -name => 'timed');
}

my @QOTD = (
    'qotd1',
    'qotd2',
    );
sub qotd {
    my $port = shift || 17;
    return new cmt::serv(
        sub {
            new cmt::stream(
                -binded => sub {
                    my $this = shift;
                    my $count = scalar(@QOTD);
                    my $index = int($count * rand);
                    my $str = $QOTD[$index];
                    $this->write($str);
                },
            )
        },
        -port => $port,
        -name => 'qotd');
}

sub chargend {
    my $port = shift || 19;
    my $serv = new cmt::serv(
        sub {
            new cmt::stream(
                -reqdata => sub {
                    my $this = shift;
                    $this->write("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ\n");
                    fsleep 0.1;
                    1
                },
            )
        },
        -port => $port,
        -name => 'chargend');
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
            # How to use IPC to share the %stats ??
            $stats{$name} = $stat;
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
