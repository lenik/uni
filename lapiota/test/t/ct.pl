#!/usr/bin/perl

use strict;
use cmt::inet;
use cmt::stream;
use cmt::util;
use cmt::vcs;
use Data::Dumper;
use Getopt::Long;

sub boot;
sub main;
sub info;
sub info2;
sub version;
sub help;

our $opt_verbtitle      = 'tcp-connect';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_host;
our $opt_port           = 80;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               );

    $opt_host   = shift @ARGV   if @ARGV;
    $opt_port   = shift @ARGV   if @ARGV;

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
    my %id = parse_id('$Id$');
    print "[$opt_verbtitle] Perl_simple_cli_program_template \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        $0 <options> [<host> [<port=80>]]

Options:
        --quiet                 (q)
        --verbose               (v, repeat to get more info)
        --version
        --help                  (h)
EOM
}

exit boot;

sub main {
    my $cn = tcp_connect($opt_host, $opt_port, new cmt::stream(
        -binded => sub {
            my $s = shift;
            info "binded ".join(',', @_);
            info "binded-stream: ".$s->hinfo;
        },
        -unbinded => sub {
            info "unbinded ".join(',', @_);
        },
        -gotdata => sub {
            info "gotdata ".join(',', @_);
        },
        -askdata => sub {
            my $s = shift;
            info "askdata ".join(',', @_);
            # my $write_t = $s->{ctx}->{STAT}->{write_t};
            # info "write_t = $$write_t";   # => inf
            #my $enter = <STDIN>;
            #$s->write($enter);
            undef;
        },
        -goterr => sub {
            info "goterr ".join(',', @_);
        },
    )) or die;

    my $res = $cn->loop;
    info "Result: ".Dumper($res);
}
