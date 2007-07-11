package fun::test;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::util;
use cmt::vcs;
use Exporter;
use Getopt::Long;

@ISA    = qw(Exporter);
@EXPORT = qw(hello
             );

sub boot;
sub info;
sub info2;
sub version;
sub help;

my $opt_verbtitle       = 'libunknown';
my $opt_verbtime        = 0;
my $opt_verbose         = 1;

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
    my %id = parse_id('$Id: .pm,v 1.5 2007-07-11 15:24:08 lenik Exp $');
    print "[$opt_verbtitle] Perl_simple_cli_libfun_template \n";
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
        ~hello  Hello world test
EOM
}

sub hello {
    info 'TODO...';
}

1