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

our $PACKAGE            = ':test';

our $opt_verbtitle      = 'libunknown';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_name;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               'name=s',
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
    my %id = parse_id('$Id: test.pm,v 1.3 2006-12-12 11:48:36 lenik Exp $');
    print "[opt_verbtitle] Perl simple cli/libfun template\n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        fun -u $PACKAGE ~$0 <options> ...

Options:
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)
EOM
}

sub hello {
    boot;
    die "name isn't specified" unless defined $opt_name;
    info "name: $opt_name";
    print "Hello, $opt_name! \n";
}

@ISA = qw(Exporter);
@EXPORT = qw(
	hello
	);

1;
