package cmt::serv;

use strict;
use Fcntl;
use IO::Socket;
use IO::Select;
use cmt::ftime;
use cmt::util;

sub nonblock;
sub fsleep;
sub info;
sub info2;

my $DEFAULT_PORT    = 51296;

sub new {
    my $class = shift;
    my ($chprov, $port, $name) = @_;

    $port       ||= $DEFAULT_PORT;
    $name       ||= $port . 'd';

    my $self = bless {}, $class;
    $self->{name}       = $name;
    $self->{address}    = 'localhost';
    $self->{port}       = $port;
    $self->{proto}      = 'tcp';            #
    $self->{chprov}     = $chprov;          # channel provider
    $self->{interval}   = 2;                # idle-loop interval
    $self->{capacity}   = 10;               # max clients allowed
    $self->{verbose}    = 0;                # disable verbose
    return $self;
}

sub name        { return shift->{name}; }
sub address     { return shift->{address}; }
sub port        { return shift->{port}; }
sub proto       { return shift->{proto}; }
sub chprov      { return shift->{chprov}; }
sub interval    { return shift->{interval}; }
sub capacity    { return shift->{capacity}; }
sub select      { return shift->{select}; }
sub verbose {
    my $self = shift;
    my $value = shift;
    $self->{verbose} = $value if defined $value;
    return $self->{verbose};
}

sub serv {
    my $self        = shift;
    my $port        = $self->port;
    my $chprov      = $self->chprov;
    my $interval    = $self->interval;    # idle timeout
    my $timeout     = $interval / 3;
    my $st_req      = 0;
    my $st_select   = 0;
    my $st_recv     = 0;
    my $st_idle     = 0;
    my $st_err      = 0;

    $self->info("initializing...");
    my $server = new IO::Socket::INET(
                # LocalAddr   => $self->address,
                MultiHomed  => 1,
                LocalPort   => $port,
                Proto       => $self->proto,
                Listen      => $self->capacity,
                );

    if (! $server) {
        $self->info("can't create server socket: $@");
        return -1;
    }

    if (! nonblock($server)) {
        $self->info("can't make socket nonblocking: $!\n");
        $server->shutdown(2);
        return -1;
    }

    my %sockinfo;
    my $select = new IO::Select($server);

    my $selread = $select;
    my $selwrite = $select;
    my $selex = $select;

    my $lastidle = ftime() - $interval * 2;

    $self->info("started");

    while ($select->count) {
        my $clients = $select->count - 1;

        # check if idle after idletimeout
        # the checking will also touch channels' can_write status.
        $selwrite = $select if (ftime() - $lastidle > $interval);

        # blocks until a handle is ready.
        # select(READ, WRITE, ERROR [, TIMEOUT])
        $! = undef;
        my $select_begin = ftime();
        my @all = IO::Select->select(
            $selread, $selwrite, $selex, $timeout);
        $st_select++;
        $self->info2("select error: $!") if $!;

        # THE BUG IS FIXED BY USING SHUTDOWN INSTEAD OF CLOSE.
        # wait more if no events, to avoid some bugs in IO::Select
        my $select_elaps = ftime() - $select_begin;
        if (! @all and $select_elaps < $timeout) {
            my $wait = $timeout - $select_elaps;
            $self->info2("wait more $wait");
            fsleep($wait);
        }

        my @rs = @all[0] ? @{$all[0]} : ();
        my @ws = @all[1] ? @{$all[1]} : ();
        my @xs = @all[2] ? @{$all[2]} : ();
        $self->info2("selected r".scalar(@rs)." w".scalar(@ws)." x".scalar(@xs));

        for my $s (@rs) {
            $self->info2("read $s");
            # accept incoming connection
            if ($s == $server) {
                my $client = $server->accept;
                # TODO - if (! $client) ...?
                $self->info("connection $st_req accepted"
                             . "(total $clients): $client");
                nonblock($client);  # ignore this error.

                # create a channel
                my $ch = &$chprov;
                $ch->_bind_($self, $client);
                $ch->fire_init($client);
                $ch->{req} = ++$st_req;

                # assoc sock with channel
                $sockinfo{$client} = $ch;

                $select->add($client);
            }

            # dispatch recv data to corresponding channel
            else {
                my $ch = $sockinfo{$s};
                my $msg = <$s>;
                if (length($msg)) {
                    $ch->fire_recv($msg);
                    $st_recv++;
                } else {
                    $self->info("remote $s is disconnected");
                    $ch->fire_uninit;
                    $s->shutdown(2);
                    $select->remove($s);
                }
            }
        }

        for my $s (@ws) {
            $self->info2("can_write $s");
            # skip server socket for "can_write" event.
            next if ($s == $server);
            my $ch = $sockinfo{$s};
            $ch->{can_write} = 1;
        }

        for my $s (@xs) {
            $self->info2("exception $s");
            # remove errored sockets.
            if ($s == $server) {
                $self->info("TODO - server error happened...");
                next;
            }
            $self->info("client $s errored, removed. ");
            my $ch = $sockinfo{$s};
            $ch->fire_uninit;
            $s->shutdown(2);
            $select->remove($s);
            $st_err++;
        }

        if (scalar(@rs) == 0 and scalar(@ws) == $clients) {
            # idle encountered.
            @all = $select->handles;
            for my $s (@all) {
                next if ($s == $server);
                $self->info2("idle $s");
                my $ch = $sockinfo{$s};
                $ch->fire_idle;
                $st_idle++;
            }
            $lastidle = ftime;
            $selwrite = undef;
        }
    }

    $self->info("exited");
    my %stat = (
        'reqs'      => $st_req,
        'selects'   => $st_select,
        'recv'      => $st_recv,
        'idle'      => $st_idle,
        'err'       => $st_err,
        );
    return \%stat;
}

sub nonblock {
    my $socket = shift;
    # my $flags = fcntl($socket, F_GETFL, 0);
    # return 0 if (! $flags);
    # return 0 if (! fcntl($socket, F_SETFL, $flags | O_NONBLOCK));
    return 1;
}

sub info {
    my $self = shift;
    if ($self->{verbose} >= 1) {
        my $name = $self->name;
        print datetime." [serv.$name] @_\n";
    }
}

sub info2 {
    my $self = shift;
    if ($self->{verbose} >= 2) {
        my $name = $self->name;
        print datetime." (serv.$name) @_\n";
    }
}

1;
