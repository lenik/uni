package fun::test;

use strict;
use cmt::util;
use cmt::vcs;
use Getopt::Long;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub boot;
sub info;
sub info2;
sub version;
sub help;

sub myfun;

@ISA = qw(Exporter);
@EXPORT = qw(
	myfun
	);

our $opt_verbtitle      = 'libunknown';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

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
    my %id = parse_id('$Id: test.pm,v 1.1 2006-12-12 11:15:57 lenik Exp $');
    print "[opt_verbtitle] Perl simple cli/libfun template\n";
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

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               );
}

sub myfun {
    boot;
    # main
}

1;
