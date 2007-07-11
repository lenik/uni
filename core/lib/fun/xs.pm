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

my $opt_verbtitle       = 'xsutil';
my $opt_verbtime        = 0;
my $opt_verbose         = 1;
my $opt_package;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               'package|p=s',
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
    my %id = parse_id('$Id: xs.pm,v 1.2 2007-07-11 15:25:09 lenik Exp $');
    print "[$opt_verbtitle] XSUB Utilities \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        fun ~command <options>...

Common Options:
        --package=<perl-package> (p, eg. "MyPkg::MySubPkg")
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)

Commands:
        ~stwrap     C-Struct Wrapper
EOM
}

sub stwrap {
    my $stname;
    my %st;

    sub stdump {
        my ($n, $s) = @_;
        info "dump: $n => $s";
    }

    while (<>) {
        if (/struct \s+ (\w+)/x) {
            stdump $stname, \%st if defined $stname;
            $stname = $1;
            undef %st;
            next;
        }
        while (/^\s* (.+) \s+ (\w+ (,\s*\w+)* ) \s* ;/g) {
            my $type = $1;
            my @mems = split(/\s*,\s*/, $2);
            info "$type: [".join('|', @mems).']';
        }
    }
    stdump $stname, \%st if defined $stname;
}

1