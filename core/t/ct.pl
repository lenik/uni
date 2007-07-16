#!/usr/bin/perl

use strict;
use Getopt::Long;
use cmt::util;
use cmt::channel;
use cmt::client;

my ($opt_host, $opt_port) = @ARGV;

sub init {
    print "Init\n";
}

sub uninit {
    print "Uninit\n";
}

sub recv {
    shift;
    my $msg = shift;
    print "Recv: [$msg]\n";
    my $line = <STDIN>;
    return $line;
}

sub idle {
    print "Idle\n";
}

my $ch = mkch(\&recv, \&idle, \&init, \&uninit);
client_comm($ch, $opt_host, $opt_port);
