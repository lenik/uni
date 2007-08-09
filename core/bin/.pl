#!/usr/bin/perl

use strict;
use cmt::util;
use cmt::vcs;
use Getopt::Long;

sub boot;
sub info;
sub info2;
sub version;
sub help;
sub main;

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
    my %id = parse_id('$Id: .pl,v 1.9 2007-08-09 13:49:15 lenik Exp $');
    print "[$opt_verbtitle] Perl_simple_cli_program_template \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print "\nSyntax: \n    $0 [OPTION] [--] ...\n", <<'EOM';

Common options:
    -h, --help              show this help page
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
        --version           print the version info
EOM
}

exit boot;

sub main {
    info "TODO...";
}
