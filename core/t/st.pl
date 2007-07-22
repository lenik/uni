
use strict;
use IO::Socket;
use IO::Select;
use cmt::serv;
use cmt::stream;
use Data::Dumper;

my $serv = new cmt::serv(
    sub {
        new cmt::stream(
            -gotdata => sub {
                my ($this, $msg) = @_;
                $this->write("Repeat - $msg");
            },
        )
    }, 7, 'echod');

$serv->verbose(1);
# print Dumper($serv);
my $stat = $serv->serv;
print Dumper($stat);


__END__

    cmt::ios
        GROUPS  => { G => \@fd, ... ]
        \init($context, $ios)
        \read($context, $fd)
        \write($context, $fd)
        \err($context, $fd)

    cmt::ios::merged
        SUBS    => [ @ios ],
        GROUPS  => [ G => \@merged_fd, ... ]
        FD_IDX  => { $fd => $ios, ... }

    cmt::ios::context
        IOS     => $ios
        READS   => IO::Select
        WRITES  => IO::Select
        ERRS    => IO::Select
        STAT    => { ... }
        NEXT    => \$next

    cmt::stream
        IO          => $fd
        \binded($stream, $fd)
        \unbinded($stream, $fd)
        \gotdata($stream, $data)
        \askdata($stream)
        \goterr($stream)

    cmt::serv
        name        => $name,
        address     => 'localhost',
        port        => $port,
        proto       => 'tcp',           #
        sfac        => $sfac,           # stream factory
        interval    => 2,               # idle-timeout
        capacity    => 10,              # max clients allowed
        verbose     => 0,               # disable verbose
