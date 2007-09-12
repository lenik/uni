#!/usr/bin/perl

=head1 NAME

UnKnOwN - Perl_simple_cli_program_template

=cut
use strict;
use cmt::log;
use cmt::vcs('parse_id');
    my %RCSID = parse_id('$Id: .pl,v 1.13 2007-09-12 17:11:06 lenik Exp $');
use Getopt::Long;

sub _main; sub _version; sub _help;

BEGIN {
    ($opt_verbtitle) = 'UnKnOwN'; # $0 =~ /([^\/\\.]+)(?:\.\w+)*$/;
    sub _log2 { $opt_verbose >= 2 && &_log }
    sub _log3 { return if $opt_verbose < 3; &_log }
    sub _sig2 { return if $opt_verbose < 2; &_sig }
    sub _sig3 { return if $opt_verbose < 3; &_sig }
}

sub boot {
    GetOptions('quiet|q'        => sub { $opt_verbose-- },
               'verbose|v'      => sub { $opt_verbose++ },
               'version'        => sub { _version; exit 0 },
               'help|h'         => sub { _help; exit 0 },
               );
    _main;
}

=head1 SYNOPSIS

B<UnKnOwN>
    S<[ B<-q> | B<--quiet> ]>
    S<[ B<-v> | B<--verbose> ]>
    S<[ B<-h> | B<--help> ]>
    S<[ B<--version> ]>
    S<[ B<--> ]>
    S<[ I<...the rest of arguments...> ]>

=head1 DESCRIPTION

B<UnKnOwN> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-UnKnOwN-RESOLVES.

=head1 OPTIONS

=over 8

=item S<[ B<-m> | B<--my-option> ]>

...

=item S<[ B<-q> | B<--quiet> ]>

Repeat this option to suppress unimportant information to display.

=item S<[ B<-v> | B<--verbose> ]>

Repeat this option to display more detailed information.

=item S<[ B<-h> | B<--help> ]>

Display a breif help page and exit(0).

=item S<[ B<--version> ]>

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
    print "\nSyntax: \n    $0 [OPTION] [--] ...\n", <<'EOM';

Common options:
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
    -h, --help              show this help page
        --version           print the version info
EOM
}

exit boot;

sub _main {
    _sig 'ARG', $_ for @ARGV;
    _log "TODO...";
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HACKING

(No Information)

=cut
# (MODULE FUNCTIONS)

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=cut
sub _version {
    print "[$opt_verbtitle] Perl_simple_cli_program_template \n";
    print "Written by Lenik,  Version 0.$RCSID{rev},  Last updated at $RCSID{date}\n";
}

=head1 SEE ALSO

The L<cmt/"Perl_simple_cli_program_template">

=head1 AUTHOR

Xima Lenik <lenik@bodz.net>

=cut
