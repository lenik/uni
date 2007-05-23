#!/usr/bin/perl

use strict;
use cmt::util;
use cmt::vcs;
use Getopt::Long;

sub boot;
sub main;
sub info;
sub info2;
sub version;
sub help;

our $opt_verbtitle      = 'unknown';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               );
    main;
}

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

sub version {
    my %id = parse_id('$Id: .pl,v 1.4 2007-05-23 14:45:04 lenik Exp $');
    print "[$opt_verbtitle] Perl simple cli program template\n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        $0 <options> ...

Options:
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)
EOM
}

boot;

sub main {
    info "TODO...";
}
