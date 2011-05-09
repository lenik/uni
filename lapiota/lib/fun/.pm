package fun::test;

use strict;
use cmt::util;
use cmt::vcs;
use fun::_booter(':info');
use Exporter;
use Getopt::Long;

our @ISA                = qw(Exporter);
our @EXPORT             = qw(hello);

sub boot;
sub info;
sub info2;
sub version;
sub help;

our $opt_verbtitle      = 'libunknown';
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
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub version {
    my %id = parse_id('$Id$');
    print "[$opt_verbtitle] Perl_funs_common_cli_template \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version unless shift;
    print <<"EOM";

Syntax:
    $funpack->{name} ~command <options>...

Common options:
    -h, --help              show this help page
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
        --version           print the version info

Commands:
    ~hello      Hello world test
EOM
}

sub hello {
    info 'TODO...';
}

1