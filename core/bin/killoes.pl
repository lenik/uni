#!/usr/bin/perl

use strict;
use cmt::path;
use cmt::util;
use cmt::vcs;
use Getopt::Long;

sub boot;
sub info;
sub info2;
sub version;
sub help;
sub _main;
sub vercmp;

our $opt_verbtitle      = 'killolds';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our %opt_fswalk         = ();

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               );
    _main;
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
    my %id = parse_id('$Id: killoes.pl,v 1.1 2007-09-29 10:32:30 lenik Exp $');
    print "[$opt_verbtitle] Perl_simple_cli_program_template \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print "\nSyntax: \n    $0 [OPTION] [--] ...\n", <<'EOM';

Common options:
    -h, --help              show this help page
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
        --version           print the version info
EOM
}

exit boot;

sub _main {
    for (@ARGV) {
        fswalk {
            my $dir = shift;
            return 0 unless -d $dir;
            my @files = listdir($dir, undef, qr/^\.\.?$/);
            my @kills;
            for (@files) {
                if (/^\@\w=\w$/) {
                    push @kills, $_;
                }
            }
            for (@kills) {
                my $path = $dir.'/'.$_;
                info "del $path";
                unlink $path;
            }
            return 1;
        } -start => $_, %opt_fswalk;
    }
}

sub vercmp {
    my ($a, $b) = @_;
    my (@a, @b, @n, @m);
    while ($a =~ /(\d+)|(\D+)/g) { push @n, $1; push @a, $2; }
    while ($b =~ /(\d+)|(\D+)/g) { push @m, $1; push @b, $2; }
    for (my $i = 0; $i < @n; $i++) {
        my $N = $n[$i];
        my $M = $m[$i];
        my $c = $N <=> $M;
        if ($N =~ /^0/ or $M =~ /^0/ or abs(length($N) - length($M)) > 1) {
            # 0501 < 06
            # 1201 < 13
            $c = $N cmp $M;
        }
        if ($c == 0) {
            next if $a[$i] eq $b[$i];
            return $a[$i] cmp $b[$i];
        } else {
            return $c;
        }
    }
    return @n <=> @m;
}
