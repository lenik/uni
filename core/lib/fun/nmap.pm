package fun::nmap;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::util;
use cmt::vcs;
use Exporter;
use Getopt::Long;

@ISA    = qw(Exporter);
@EXPORT = qw(macs
             );

sub opts;
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
    my %id = parse_id('$Id: nmap.pm,v 1.1 2007-07-10 15:43:00 lenik Exp $');
    print "[$opt_verbtitle] Perl_simple_cli_libfun_template \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        fun ~command <options>...

Common Options:
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)

Commands:
        ~macs   Nmap (-sP) MAC List
EOM
}

sub macs {
    my ($host, $hostname, $up, $mac, $spec);
    while (<>) {
        # Host 192.168.1.40 appears to be up.
        # Host hnlly (192.168.1.38) appears to be up.
        # MAC Address: 00:01:6C:8E:1A:B0 (Foxconn)
        if (/^Host (.+) appears to be (up|down)/) {
            $host = $1;
            $up = $2 eq 'up' ? 1 : 0;
            if ($host =~ /^(\S+) \((\S+)\)$/) {
                ($host, $hostname) = ($2, $1);
            }
        } elsif (/^MAC Address: (\S+)(?: \(([^)]+)\))?/) {
            $mac = $1;
            $spec = $2;
            $mac =~ s/://g;
            print "$mac:$host:$hostname:$up:$spec\n";

            undef $host;
            undef $hostname;
            undef $up;
        }
    }
}

1