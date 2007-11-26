package cmt::serv;

=head1 NAME

cmt::serv - Simple Network Service Class

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::ftime;
use cmt::ios;
use cmt::log(2);
use cmt::netutil;
use cmt::stream;
use cmt::time('cdatetime');
use cmt::util('get_named_args');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: .pm,v 1.7 2007-09-14 16:09:45 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use Fcntl;
use IO::Socket;

our @ISA    = qw(Exporter);
our @EXPORT = qw(mysub
                 );

# INITIALIZORS
our $DEFAULT_PORT   = 51296;
our $DEFAULT_CAP    = 10;
our $DEFAULT_INTV   = 2;

=head1 SYNOPSIS

    use cmt::serv;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::serv> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::serv-RESOLVES.

=head1 METHODS

=cut
sub new {
    my $class       = shift;
    my $sfac        = shift;
    my $this = {
        addr        => 'localhost',
        port        => $DEFAULT_PORT,
        proto       => 'tcp',           #
        sfac        => $sfac,           # stream factory
        intv        => $DEFAULT_INTV,   # interval (idle-timeout)
        cap         => $DEFAULT_CAP,    # max clients allowed
        verbose     => $LOGLEVEL,
    };
    get_named_args @_, %$this, 1;
    $this->{'name'} = $this->{'port'}.'d'
        unless defined $this->{'name'};
    bless $this, $class;
}

sub name            { return shift->{'name'}; }
sub address         { return shift->{'addr'}; }
sub port            { return shift->{'port'}; }
sub proto           { return shift->{'proto'}; }
sub sfac            { return shift->{'sfac'}; }
sub interval        { return shift->{'intv'}; }
sub capacity        { return shift->{'cap'}; }
sub select          { return shift->{'select'}; }

sub create_ios {
    my $this        = shift;
    my $port        = $this->{'port'};
    my $sfac        = $this->{'sfac'};
    my $intv        = $this->{'intv'};

    my $st_seq      = 0;
    my $st_select   = 0;
    my $st_push     = 0;
    my $st_pull     = 0;
    my $st_err      = 0;

    $this->log2("initializing...");
    my $server = new IO::Socket::INET(
      # LocalAddr   => $this->{'addr'},
        MultiHomed  => 1,
        LocalPort   => $port,
        Proto       => $this->{'proto'},
        Listen      => $this->{'cap'},
    );

    if (! $server) {
        $this->log1("can't create server socket: $@");
        return -1;
    }

    if (! setnonblock($server)) {
        $this->log1("can't make socket nonblocking: $!\n");
        $server->shutdown(2);
        return -1;
    }

    my $streams = {};
    my $clients = 0;

    new cmt::ios(
        READ    => [$server],   # skip server socket for "can_write" event.
        WRITE   => [],
        ERR     => [$server],   # server & clients
        -read   => sub { *__ANON__ = '<read>';
            my $ctx     = shift;
            my $client  = shift;

            # accept incoming connection
            if ($client == $server) {
                my $client          = $server->accept;
                # TODO - if (! $client) ...?
                $this->log2("connection $st_seq accepted(total $clients): $client");

                setnonblock($client);           # (FIX...)

                my $stream          = $sfac->();
                $streams->{$client} = $stream;
                $stream->{seq}      = ++$st_seq;
                $stream->{ctx}      = $ctx;
                $stream->bind($client);

                $ctx->add_main($client);
            }

            # dispatch recv data to corresponding channel
            else {
                my $stream  = $streams->{$client};
                my $msg     = $stream->read;    # non-block
                if (length($msg)) {
                    my $resp = $stream->push($msg);
                    $st_push++;
                } else {
                    $this->log2("remote ".sockinfo($client)." is disconnected");
                    $stream->shutdown(2);
                    $stream->unbind;
                    $ctx->remove_main($client);
                }
            }
        },
        -write  => sub { *__ANON__ = '<write>';
            my $ctx     = shift;
            my $client  = shift;
            my $stream  = $streams->{$client};
            my $resp    = $stream->pull();
            $st_pull++;
            return $resp;
        },
        -err    => sub { *__ANON__ = '<err>';
            my $ctx     = shift;
            my $client  = shift;

            $this->log2("exception $client");
            # remove errored sockets.
            if ($client == $server) {
                $this->log2("TODO - server error happened...");
                # next;
            }
            $this->log2("client $client errored, removed. ");

            my $stream  = $streams->{$client};
            my $resp    = $stream->err();
            # TO DO with resp ?...

            $stream->shutdown(2);
            $stream->unbind;
            $ctx->remove($client);
            $st_err++;
        },
    );
}

sub serv {
    my $this = shift;
    my $ios = $this->create_ios(@_);
    $this->log2("started");
    $ios->loop;
}

# utilities

sub verbose {
    my $this = shift;
    my $value = shift;
    $this->{verbose} = $value if defined $value;
    return $this->{verbose};
}

sub log1 {
    my $this = shift;
    if ($this->{verbose} >= 1) {
        my $name = $this->name;
        print cdatetime." [serv.$name] @_\n";
    }
}

sub log2 {
    my $this = shift;
    if ($this->{verbose} >= 2) {
        my $name = $this->name;
        print cdatetime." (serv.$name) @_\n";
    }
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

The L<cmt/"Simple Network Service Class">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1