package fun::test;

use strict;
use cmt::util;
use cmt::vcs;
use cmt::win32;
use Getopt::Long;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub boot;
sub info;
sub info2;
sub version;
sub help;

our $PACKAGE            = ':test';

our $opt_verbtitle      = 'test';
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
    my %id = parse_id('$Id: test.pm,v 1.5 2007-07-11 15:24:47 lenik Exp $');
    print "[$opt_verbtitle] Test fun \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        fun ~<command> <options> ...

Common Options:
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)

Commands:
        ~hello  name
        ~api    "X Lib::Fun(XXX)" ...
        ~api    Lib:Fun:XXX:X ...
        ~api    Lib Fun XXX X - ...
        ~api    Lib "X Fun(XXX)" - ...
EOM
}

sub hello {
    unless (defined $opt_name) {
        $opt_name = shift;
        die "name isn't specified" unless defined $opt_name;
    }
    info2 "name: $opt_name";
    info "Hello, $opt_name! ";
}

sub api {
    # fun/api ApiDecl args
    # fun/api ApiDecl... - args
    my @decl;
    for my $i (0..$#_) {
        if ($_[$i] eq '-') {
            @decl = splice(@_, 0, $i);
            shift @_;
            last;
        }
    }
    push @decl, shift unless @decl;
    info2 'ApiDecl: '.join(' - ', @decl);
    info2 'ApiArgs: '.join(', ', @_);
    my $ret = API(@decl)->(@_);
    info 'Return: '.$ret;
    return $ret;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	hello
    api
	);

1;
