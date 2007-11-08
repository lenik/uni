package fun::test;

use strict;
use cmt::util;
use cmt::vcs;
use cmt::win32;
use fun::_booter(':info');
use Exporter;
use Getopt::Long;

our @ISA    = qw(Exporter);
our @EXPORT = qw(hello
                 api);

sub boot;
sub info;
sub info2;
sub version;
sub help;

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
    my %id = parse_id('$Id: test.pm,v 1.7 2007-11-08 10:52:38 lenik Exp $');
    print "[$opt_verbtitle] Test fun \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
    $funpack->{name} ~<command> <options> ...

Common options:
    -h, --help              show this help page
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
        --version           print the version info

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

1