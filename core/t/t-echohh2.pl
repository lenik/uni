
use strict;
use Socket;
use Getopt::Long;
use cmt::vcs;
use cmt::echohh;
use Data::Dumper;

my $opt_host = "lenik.vicp.net";
    my ($name, $alias, $addrtype, $len, $addr)
        = gethostbyname($opt_host);
    my @ip = unpack("C4", $addr);
    my $addr = join('.', @ip);

    print "$name, $alias, $addrtype, $len, $addr";
