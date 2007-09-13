package cmt::log;

use strict;
use cmt::util;
use Exporter;

# global config:
our $opt_verbtitle      = 'unknown';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(_log
                 _sig
                 _sigx
                 $opt_verbtitle
                 $opt_verbtime
                 $opt_verbose
                 );

sub _log {
    return if $opt_verbose < 1;
    print STDERR datetime.' ' if $opt_verbtime;
    print STDERR "[$opt_verbtitle] ", @_, "\n";
}

sub _sig {
    return if $opt_verbose < 1; my $cls = shift; local $_ = join('', @_);
    return unless -t STDERR or s/\n$//s;
    printf STDERR "[%4s] %-72s".(-t STDERR ? "\r" : "\n"), $cls, $_;
}

sub _sigx {
    return if $opt_verbose < 1;
    print STDERR (-t STDERR ? '' : "\n"), '    err: ', @_, "\n";
}

1