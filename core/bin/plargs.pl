#!/usr/bin/perl

=head1 NAME

plargs - Print the actual arguments passed to the perl program

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = 'plargs'; # $0 =~ /([^\/\\.]+)(?:\.\w+)*$/;
use cmt::log(2);
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: .pl,v 1.16 2007-09-29 11:31:33 lenik Exp $');
use Data::Dumper;
use Getopt::Long;

sub _main; sub _version; sub _help;
sub dumpargs;

sub boot {
    _P1 "Before GetOptions: ";
    dumpargs(\@ARGV);

    GetOptions('quiet|q'        => sub { $LOGLEVEL-- },
               'verbose|v'      => sub { $LOGLEVEL++ },
               'version'        => sub { _version; exit 0 },
               'help|h'         => sub { _help; exit 0 },
               );

    _P1 "After GetOptions: ";
    dumpargs(\@ARGV);

    _main;
}

=head1 SYNOPSIS

B<plargs>
    S<[ B<-q> | B<--quiet> ]>
    S<[ B<-v> | B<--verbose> ]>
    S<[ B<-h> | B<--help> ]>
    S<[ B<--version> ]>
    S<[ B<--> ]>
    S<[ I<...the rest of arguments...> ]>

=head1 DESCRIPTION

B<plargs> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-plargs-RESOLVES.

=head1 OPTIONS

=over 8

=item B<-m> | B<--my-option>

...

=item B<-q> | B<--quiet>

Repeat this option to suppress unimportant information to display.

=item B<-v> | B<--verbose>

Repeat this option to display more detailed information.

=item B<-h> | B<--help>

Display a breif help page and exit(0).

=item B<--version>

Display a short version information and exit(0).

=back

=head1 ENVIRONMENT

=over 8

=item TEMP, TMP

TEMP(or TMP, if $TEMP directory isn't existed) directory used to create
temporary files.

=back

=cut
sub _help {
    &_version;
    print "\nSyntax: \n    $0 [OPTION] [--] ARGUMENTS\n", <<'EOM';

Common options:
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
    -h, --help              show this help page
        --version           print the version info
EOM
}

exit boot;

sub _main {
    # Do nothing.
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HACKING

(No Information)

=cut
# (MODULE FUNCTIONS)
sub dumpargs {
    my $array = shift;
    for (0..$#$array) {
        _PF1 "%4d. `%s'", $_, $array->[$_];
    }
    _P1 Dumper($array);
}

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=cut
sub _version {
    print "[$LOGNAME] Print the actual arguments passed to the perl program \n";
    print "Written by Lenik,  Version 0.$RCSID{rev},  Last updated at $RCSID{date}\n";
}

=head1 SEE ALSO

The L<cmt/"Print the actual arguments passed to the perl program">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
