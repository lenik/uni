
package cmt::service;

use strict;
use cmt::ftime;
use Win32::Service;
use Win32::Daemon;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub _def2daemon;

my $DEFAULT_TIMEOUT     = 3.0;


sub service_install {
    my ($def, $modify, $machine) = @_;

    my $hash = _def2daemon($def);
    $hash->{machine} = $machine;

    my $ok;
    if ($modify) {
        $ok = Win32::Daemon::ConfigureService($hash);
    } else {
        $ok = Win32::Daemon::CreateService($hash);
    }

    if (not $ok) {
        $@ = Win32::Daemon::GetLastError;
        $! = Win32::FormatMessage $@;
    }
    return $ok;
}

sub service_uninstall {
    my $name = shift;
    my $machine = shift;

    my $ok;
    if (defined $machine) {
        $ok = Win32::Daemon::DeleteService($machine, $name);
    } else {
        $ok = Win32::Daemon::DeleteService($name);
    }

    if (not $ok) {
        $@ = Win32::Daemon::GetLastError;
        $! = Win32::FormatMessage($@);
    }
    return $ok;
}

sub service_command {
    my ($name, $cmd, $host, $timeout) = @_;
    $timeout ||= $DEFAULT_TIMEOUT;

    my $begin = ftime;
    my $ref;
    my $tstate;
    my $ret;
    if ('start' eq $cmd) {
        $ref = \&Win32::Service::StartService;
        $tstate = 'started';
    } elsif ('stop' eq $cmd) {
        $ref = \&Win32::Service::StopService;
        $tstate = 'stopped';
    } elsif ('restart' eq $cmd) {
        return service_restart($name, $host, $timeout);
    } elsif ('pause' eq $cmd) {
        $ref = \&Win32::Service::PauseService;
        $tstate = 'paused';
    } elsif ('resume' eq $cmd) {
        $ref = \&Win32::Service::ResumeService;
        $tstate = 'started';
    } else {
        die "Unknown service command: $cmd";
    }

    $ret = -1;
    my $tries = 0;
    while ((ftime() - $begin) < $timeout) {
        my $state = service_state($name, $host);
        if ($state eq $tstate) {            # if already done.
            $ret = 1;
            last;
        }
        $ret = &$ref($host, $name);
        $tries++;
        # last if $ret;                     # only the tstate is tested.
        fsleep(0.001);
    }
    if ($ret == -1) {
        $ret = 0;
        $! = 'Timeout';
    }
    if ($tries == 0) {
        $ret = 2;
        $! = 'already ' . $tstate;
    }
    return $ret;
}

sub service_start {
    my $name = shift;
    return service_command($name, 'start', @_);
}

sub service_stop {
    my $name = shift;
    return service_command($name, 'stop', @_);
}

sub service_restart {
    service_stop(@_);
    service_start(@_);
}

sub service_pause {
    my $name = shift;
    return service_command($name, 'pause', @_);
}

sub service_resume {
    my $name = shift;
    return service_command($name, 'resume', @_);
}

sub service_list {
    my ($host, $dispkey) = @_;
    my %hash;
    Win32::Service::GetServices($host, \%hash);
    return \%hash if $dispkey;

    my %rev;
    for my $k (keys %hash) {
        my $v = $hash{$k};
        $rev{$v} = $k;
    }
    return \%rev;
}

sub service_status {
    my ($name, $host) = @_;
    my %hash;
    Win32::Service::GetStatus($host, $name, \%hash);
    return \%hash;
}

my @states = (
    'stopping', 'stopped', 'starting', 'state3',
    'started', 'state5', 'state6', 'state7', );

sub service_state {
    my $hash = service_status(@_);
    my $st = $hash->{CurrentState};
    return $states[$st];
}

sub _def2daemon {
    my $def = shift;
    # Keep Compatible.
    my %stru = (
        name            => $def->{name},
        display         => $def->{display},
        description     => $def->{description},
        path            => $def->{path},
        parameters      => $def->{parameters},
        user            => $def->{user},
        password        => $def->{password},
        service_type    => $def->{service_type},
        start_type      => $def->{start_type},
        error_control   => $def->{error_control},
        load_order      => $def->{load_order},
        tag_id          => $def->{tag_id},
        dependencies    => $def->{dependencies},
        );
    return \%stru;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	service_install
	service_uninstall
	service_command
	service_start
	service_stop
	service_restart
	service_pause
	service_resume
	service_list
	service_status
	service_state
	);

1;
