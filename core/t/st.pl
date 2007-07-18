
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
