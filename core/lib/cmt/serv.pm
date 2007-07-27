package cmt::serv;

use strict;
use cmt::ftime;
use cmt::ios;
use cmt::netutil;
use cmt::stream;
use cmt::util;
use Fcntl;
use IO::Socket;

my $DEFAULT_PORT    = 51296;

sub new {
    my $class       = shift;
    my $sfac        = shift;
    my $port        = shift || $DEFAULT_PORT;
    my $name        = shift || $port.'d';
    bless {
        name        => $name,
        address     => 'localhost',
        port        => $port,
        proto       => 'tcp',           #
        sfac        => $sfac,           # stream factory
        interval    => 2,               # idle-timeout
        capacity    => 10,              # max clients allowed
        verbose     => 1,               # disable verbose
    }, $class;
}

sub name            { return shift->{name}; }
sub address         { return shift->{address}; }
sub port            { return shift->{port}; }
sub proto           { return shift->{proto}; }
sub sfac            { return shift->{sfac}; }
sub interval        { return shift->{interval}; }
sub capacity        { return shift->{capacity}; }
sub select          { return shift->{select}; }

sub create_ios {
    my $this        = shift;
    my $port        = $this->port;
    my $sfac        = $this->sfac;
    my $interval    = $this->interval;

    my $st_seq      = 0;
    my $st_select   = 0;
    my $st_push     = 0;
    my $st_pull     = 0;
    my $st_err      = 0;

    $this->info2("initializing...");
    my $server = new IO::Socket::INET(
      # LocalAddr   => $this->address,
        MultiHomed  => 1,
        LocalPort   => $port,
        Proto       => $this->proto,
        Listen      => $this->capacity,
    );

    if (! $server) {
        $this->info("can't create server socket: $@");
        return -1;
    }

    if (! setnonblock($server)) {
        $this->info("can't make socket nonblocking: $!\n");
        $server->shutdown(2);
        return -1;
    }

    my $streams = {};
    my $clients = 0;

    new cmt::ios(
        READ    => [$server],   # skip server socket for "can_write" event.
        WRITE   => [],
        ERR     => [$server],   # server & clients
        -read   => sub {
            my $ctx     = shift;
            my $client  = shift;

            # accept incoming connection
            if ($client == $server) {
                my $client          = $server->accept;
                # TODO - if (! $client) ...?
                $this->info2("connection $st_seq accepted(total $clients): $client");

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
                    $this->info2("remote ".sockinfo($client)." is disconnected");
                    $stream->shutdown(2);
                    $stream->unbind;
                    $ctx->remove_main($client);
                }
            }
        },
        -write  => sub {
            my $ctx     = shift;
            my $client  = shift;
            my $stream  = $streams->{$client};
            my $resp    = $stream->pull();
            $st_pull++;
            return $resp;
        },
        -err    => sub {
            my $ctx     = shift;
            my $client  = shift;

            $this->info2("exception $client");
            # remove errored sockets.
            if ($client == $server) {
                $this->info2("TODO - server error happened...");
                # next;
            }
            $this->info2("client $client errored, removed. ");

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
    $this->info2("started");
    $ios->loop;
}

# utilities

sub verbose {
    my $this = shift;
    my $value = shift;
    $this->{verbose} = $value if defined $value;
    return $this->{verbose};
}

sub info {
    my $this = shift;
    if ($this->{verbose} >= 1) {
        my $name = $this->name;
        print datetime." [serv.$name] @_\n";
    }
}

sub info2 {
    my $this = shift;
    if ($this->{verbose} >= 2) {
        my $name = $this->name;
        print datetime." (serv.$name) @_\n";
    }
}

1;
