package fun::xs;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::util;
use cmt::vcs;
use Exporter;
use Getopt::Long;

@ISA    = qw(Exporter);
@EXPORT = qw(stwrap
             );

sub boot;
sub info;
sub info2;
sub version;
sub help;

our $opt_verbtitle      = 'xsutil';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               );
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
    my %id = parse_id('$Id: xs.pm,v 1.1 2007-07-10 15:43:00 lenik Exp $');
    print "[$opt_verbtitle] XSUB Utilities \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        fun ~command <options>...

Common Options:
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)

Commands:
        ~stwrap     C-Struct Wrapper
EOM
}

sub stwrap {

    while (<>) {
    }
}

1