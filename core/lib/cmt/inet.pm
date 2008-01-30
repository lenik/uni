package cmt::inet;

=head1 NAME

cmt::inet - Internet Client Functions

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::ios;
use cmt::log(2);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::stream;
use cmt::util('get_named_args');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use IO::Socket;

our @ISA    = qw(Exporter);
our @EXPORT = qw(tcp_connect
                 tcp_connect_http
                 tcp_connect_sock4
                 tcp_connect_sock5);

# Not used.
our $PROTO_TCP  = getprotobyname("tcp");
our $PROTO_UDP  = getprotobyname("udp");

# INITIALIZORS

=head1 SYNOPSIS

    use cmt::inet;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::inet> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::inet-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub tcp_connect {
    my %props = get_named_args(@_);
    my ($host, $port, $stream) = @_;

    my $cont = $props{-cont};

    _log2 "tcp connect to $host:$port";

    my $sock = new IO::Socket::INET(
        PeerAddr    => $host,
        PeerPort    => $port,
        Type        => SOCK_STREAM,
        Proto       => 'tcp',
        # %props,     # interface-binding, etc..
    );
    if (! $sock) {
        _log2 "can't connect: $!";
        return undef;
    }

    _log2 "connected.";
    $sock->autoflush(1);            # as "$| = 1" does.
    $stream->bind($sock);

    my $g = [ $sock ];
    my $ios = new cmt::ios(
        READ    => $g,
        WRITE   => $g,
        ERR     => $g,
        -init   => sub { *__ANON__ = '<init>';
            my $ctx         = shift;
            $stream->{ctx}  = $ctx; # convention, see also cmt::serv
        },
        -read   => sub { *__ANON__ = '<read>';
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
        -write  => sub { *__ANON__ = '<write>';
            my ($ctx, $fd)  = @_;
            $stream->pull();
        },
        -err    => sub { *__ANON__ = '<err>';
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
        -init => sub { *__ANON__ = '<init>';
            ($ctx, $ios) = @_;
        },
        -gotdata => sub { *__ANON__ = '<gotdata>';
            my $this = shift;       # assert $_[1] == $this->{IN/OUT}
            my $data = $this->sysread;
            1
        },
        -askdata => sub { *__ANON__ = '<askdata>';
            my $this = shift;       # assert $_[1] == $this->{IN/OUT}
        },
        -goterr => sub { *__ANON__ = '<goterr>';
            my $this = shift;       # assert $_[1] == $this->{IN/OUT}
            $this->log1("goterr: $!, going to shutdown");
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

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Internet Client Functions">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1