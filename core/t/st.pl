
use strict;
use IO::Socket;
use IO::Select;
use cmt::serv;
use cmt::channel;
use Data::Dumper;

sub echod_recv {
    my $self = shift;
    my ($msg) = @_;
    $self->send("echo - $msg");
}

my $chprov = mkchprov \&echod_recv;
my $serv = new cmt::serv($chprov, 7, 'echod');
$serv->verbose(1);
# print Dumper($serv);
my $stat = $serv->serv;
print Dumper($stat);
