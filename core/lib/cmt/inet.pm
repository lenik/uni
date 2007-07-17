package cmt::client;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::channel;
use cmt::ios;
use cmt::util;
use Exporter;
use IO::Select;
use IO::Socket;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

# Not used.
our $PROTO_TCP          = getprotobyname("tcp");
our $PROTO_UDP          = getprotobyname("udp");

@ISA    = qw(Exporter);
@EXPORT = qw(tcp_connect
             tcp_connect_http
             tcp_connect_sock4
             tcp_connect_sock5
             );

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub tcp_connect {
    my ($ch, $host, $port) = @_;
    my $sendbuf;

    info "tcp connect to $host:$port";

    # TODO - Ignore the bind-address/port options
    # TODO - more controls, UDP, etc.
    my $sock = new IO::Socket::INET(
        PeerAddr    => $host,
        PeerPort    => $port,
        Type        => SOCK_STREAM,
        Proto       => 'tcp'
        );
    if (! $sock) {
        info "Can't connect: $!";
        return 0;
    }
    info "connected.";
    $sock->autoflush(1);        # as "$| = 1" does.
    $ch->_bind_(undef, $sock);
    $ch->fire_init($sock);

    my $ios = new cmt::ios(
        net     => [ $sock ],
        -read   => sub {
            my ($ctx, $fd) = @_;
            my $in = <$fd>;
            $ch->fire_recv($in);
        },
        -write  => sub {
            my ($ctx, $fd) = @_;
            $ch->fire_send($fd);
        },
        -err    => sub {
            my ($ctx, $fd) = @_;
        },
        );
    $ios->loop('net', 'net', 'net');
}

sub tcp_connect_http {
    my ($ch, $host, $port, $proxyhost, $proxyport) = @_;
    # tcp_connect($ch, $proxyhost, $proxyport);
}

sub tcp_connect_sock4 {
    my ($ch, $host, $port, $proxyhost, $proxyport) = @_;
    # tcp_connect($ch, $proxyhost, $proxyport);
}

sub tcp_connect_sock5 {
    my ($ch, $host, $port, $proxyhost, $proxyport) = @_;
    # tcp_connect($ch, $proxyhost, $proxyport);
}

1