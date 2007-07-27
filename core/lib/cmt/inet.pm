package cmt::inet;

use strict;
use vars                qw/@ISA @EXPORT/;
use cmt::ios;
use cmt::stream;
use cmt::util;
use Exporter;
use IO::Socket;

sub info;
sub info2;

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

# utilities

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

# static methods

sub tcp_connect {
    my %props = get_named_args(@_);
    my ($host, $port, $stream) = @_;

    my $cont = $props{-cont};

    info2 "tcp connect to $host:$port";

    my $sock = new IO::Socket::INET(
        PeerAddr    => $host,
        PeerPort    => $port,
        Type        => SOCK_STREAM,
        Proto       => 'tcp',
        # %props,     # interface-binding, etc..
    );
    if (! $sock) {
        info2 "can't connect: $!";
        return undef;
    }

    info2 "connected.";
    $sock->autoflush(1);            # as "$| = 1" does.
    $stream->bind($sock);

    my $g = [ $sock ];
    my $ios = new cmt::ios(
        READ    => $g,
        WRITE   => $g,
        ERR     => $g,
        -init   => sub {
            my $ctx         = shift;
            $stream->{ctx}  = $ctx; # convention, see also cmt::serv
        },
        -read   => sub {
            my ($ctx, $fd)  = @_;
            my $data        = $stream->read($fd); # non-block
            if (length($data) == 0) {
                # closed
                $stream->shutdown(2);
                $stream->unbind;
                $ctx->exit unless $cont;
            } else {
                $stream->push($data);
            }
        },
        -write  => sub {
            my ($ctx, $fd)  = @_;
            $stream->pull();
        },
        -err    => sub {
            my ($ctx, $fd)  = @_;
            $stream->shutdown(2);
            $stream->unbind;
            unless ($cont) {
                $ctx->exit unless $stream->err($!);
            }
        },
        );
    $ios->{HANDLE} = $sock;
    $ios->{STREAM} = $stream;
    return $ios;
}

sub tcp_connect_http {
    my ($host, $port, $proxyhost, $proxyport, $stream, @props) = @_;
    my ($ctx, $ios);
    my $err;
    my $s = new ios::stream(
        -init => sub {
            ($ctx, $ios) = @_;
        },
        -gotdata => sub {
            my $this = shift;       # assert $_[1] == $this->{IN/OUT}
            my $data = $this->sysread;
            1
        },
        -askdata => sub {
            my $this = shift;       # assert $_[1] == $this->{IN/OUT}
        },
        -goterr => sub {
            my $this = shift;       # assert $_[1] == $this->{IN/OUT}
            $this->info("goterr: $!, going to shutdown");
            $ctx->exit;
            $err = "Error when communicating with proxy server $proxyhost:$proxyhost";
        },
    );
    return tcp_connect($proxyhost, $proxyport, $s, @props);
}

sub tcp_connect_sock4 {
}

sub tcp_connect_sock5 {
}

1