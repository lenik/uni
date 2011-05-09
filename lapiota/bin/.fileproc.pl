#!/usr/bin/perl

=head1 NAME

UnKnOwN - DeScRiPtIoN

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::fileproc('/[$%]/', 'batch_main');
use cmt::log(3);
    $LOGNAME    = 'UnKnOwN'; # $0 =~ /([^\/\\.]+)(?:\.\w+)*$/;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
use Getopt::Long;

sub _main; sub _version; sub _help;
sub _fmain;

our $opt_ignore_case;

sub _boot {
    GetOptions('quiet|q'        => sub { $LOGLEVEL-- },
               'verbose|v'      => sub { $LOGLEVEL++ },
               'version'        => sub { _version; exit 0 },
               'help|h'         => sub { _help; exit 0 },
               %COMOPT,         # bcdfikrtw
               );
    $opt_ignore_case = $cmt::fileproc::opt_ignore_case;
    $cmt::fileproc::LOGLEVEL = $LOGLEVEL - 1;

    die "no files specified." unless @ARGV;
    # @ARGV = qw(.) unless @ARGV;

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
    print "\nSyntax: \n    $0 [OPTION] [--] ...\n";
    print "\nCommon options: \n$COMOPT", <<'EOM';
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
    -h, --help              show this help page
        --version           print the version info

EOM
    print $WALKOPT;
}

exit (_boot or 0);

sub _main {
    my $userparam = undef;
    batch_main [\&_fmain, $userparam], @ARGV;
}

sub _fmain {
    my ($path, $tmph, $tmpf, $proc) = @_;
    0
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
    print "[$LOGNAME] DeScRiPtIoN \n";
    print "Written by Lenik,  Version 0.$RCSID{rev},  Last updated at $RCSID{date}\n";
}

=head1 SEE ALSO

The L<cmt/"Perl_simple_cli_program_template">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
