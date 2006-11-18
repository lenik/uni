package cmt::dnsutil;

use strict;
use Socket;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub resolv {
    my $name = shift;

    # ($name,$aliases,$addrtype,$length,@addrs) = gethost*
    my @host = gethostbyname($name)
        or return undef;

    my @addrs;
    for (@host[4..$#host]) {
        my $addr = inet_ntoa($_);
        push @addrs, $addr;
    }

    return wantarray ? @addrs : $addrs[0];
}

sub conf_replace_ip {
    my ($line, $name) = @_;
    if ($line =~ m/\d+\.\d+\.\d+\.\d+/) {
        my $addr = resolv($name);

        my $oldlen = length($&);
        my $pad = $oldlen - length($addr);
        $addr .= substr('                   ', -$pad) if $pad > 0;
        $line = $` . $addr . $';
        return $line;
    }
    return $line;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	resolv
	conf_replace_ip
	);

1;
