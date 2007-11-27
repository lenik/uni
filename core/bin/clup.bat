@rem = '
@echo off

    setlocal

    if not "%1"=="" cd %1

    set clflags=-P --group-within-date --hide-filenames
    set clpost=

    if exist .clflags (
        set clflags=
        for /f "delims=;" %%i in (.clflags) do (
            set x=%%i
            if "!x:~0,2!"=="#?" (
                set clpost=!clpost!!x:~2!
            ) else (
                set clflags=!clflags! !x!
            )
        )
    )

    if not "%clpost%"=="" (
        cvs log | cvs2cl --stdin --stdout %clflags% | perl "%~dpnx0" %clpost% >ChangeLog
    ) else (
        cvs log | cvs2cl --stdin %clflags%
    )

    cvs ci -m "" ChangeLog
    goto end

    ';

#!/usr/bin/perl

use strict;
use cmt::util;
use cmt::vcs;
use Getopt::Long;

sub _boot;
sub info;
sub info2;
sub version;
sub help;
sub lenik;

our $opt_verbtitle      = 'clup:util';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_style;
our $opt_abbreviate     = 0;

sub _boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               'lenik'      => sub { $opt_style = 'lenik' },
               'abbreviate',
               );
    die "style isn't specified. " unless defined $opt_style;
    die "style $opt_style isn't supported. " unless main->can($opt_style);
    my $func = eval('\&'.$opt_style);
    $func->();
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
    my %id = parse_id('$Id: clup.bat,v 1.4 2007-11-08 10:52:37 lenik Exp $');
    print "[$opt_verbtitle] clup bundled utility \n";
    print "Written by Lenik,  Version 0.$id{rev},  Last updated at $id{date}\n";
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
        --lenik (lenik-style)
        --abbreviate (print "etc." if too many files in one header)
EOM
}

boot;

sub lenik {
    my $day_idx = 0;
    my $cont = 0;

    while (<>) {
        if (s/^\t\* /  * /) {
            if ($cont = /,\s*$/) {
                s/$/ e.t.c.../ if $opt_abbreviate;
            }
            print "\n" if $day_idx++;
            print;
        } elsif ($cont) {
            s/^\t/  /;
            $cont = /,\s*$/;
            print unless $opt_abbreviate;
        } elsif (/^\d\d+-\d+-\d+$/) {
            print "\n$_";
            $day_idx = 0;
        } elsif (/^$/) {
        } else {
            print;
        }
    }
}

__END__
:end
