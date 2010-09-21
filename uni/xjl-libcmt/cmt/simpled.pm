
# simple tcp servers

package cmt::simpled;

use strict;
use cmt::ftime('fsleep');
use cmt::serv;
use cmt::stream;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub echod {
    my $port = shift || 7;
    return new cmt::serv(
        sub { *__ANON__ = '<echod-sfac>';
            new cmt::stream(
                -gotdata => sub { *__ANON__ = '<echod-gotdata>';
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
        sub { *__ANON__ = '<discard-sfac>';
            new cmt::stream(
                -gotdata => sub { *__ANON__ = '<discard-gotdata>';
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
        sub { *__ANON__ = '<timed-sfac>';
            new cmt::stream(
                -binded => sub { *__ANON__ = '<timed-binded>';
                    my $s = shift;
                    my $str = gmtime;
                    $s->write($str);
                },
                # ???
                -sent => sub { *__ANON__ = '<timed-sent>';
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
        sub { *__ANON__ = '<qotd-sfac>';
            new cmt::stream(
                -binded => sub { *__ANON__ = '<qtod-binded>';
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
        sub { *__ANON__ = '<chargend-sfac>';
            new cmt::stream(
                -reqdata => sub { *__ANON__ = '<chargend-reqdata>';
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
