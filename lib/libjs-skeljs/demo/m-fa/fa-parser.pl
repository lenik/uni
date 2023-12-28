#!/usr/bin/perl

=head1 NAME

j - 
=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    $LOGNAME    = 'j'; # $0 =~ /([^\/\\.]+)(?:\.\w+)*$/;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: - @VERSION@ @DATE@ @TIME@ - $');
use Getopt::Long;
    Getopt::Long::Configure('gnu_getopt');

sub _main; sub _version; sub _help;

sub _boot {
    GetOptions('q|quiet'        => sub { $LOGLEVEL-- },
               'v|verbose'      => sub { $LOGLEVEL++ },
               'version'        => sub { _version; exit 0 },
               'h|help'         => sub { _help; exit 0 },
               )
        or exit(1);
    _main;
}

=head1 SYNOPSIS

B<j>
    S<[ B<-q> | B<--quiet> ]>
    S<[ B<-v> | B<--verbose> ]>
    S<[ B<-h> | B<--help> ]>
    S<[ B<--version> ]>
    S<[ B<--> ]>
    S<[ I<...the rest of arguments...> ]>

=head1 DESCRIPTION

B<j> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-j-RESOLVES.

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

=head1 EXAMPLES

=over 4

=item Show help

    j --help

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
    -q, --quiet             Repeat to get less info
    -v, --verbose           Repeat to get more info
    -h, --help              Show this help page
        --version           Print the version info
EOM
}

exit (_boot or 0);

sub _main {
    my $line = 0;
    my ($ch, $name, $unicode, $ver);
    my $count = 0;
    print "var fa_list = [\n";
    while (<>) {
        my $i = $line++ % 8;
        chop;
        if ($i == 1) {
            $ver = $1 if /([0-9.]+)/;
        } elsif ($i == 2) {
            $ch = $1 if />(.*?)<\/i/;
        } elsif ($i == 3) {
            s/\s//sg;
            s/^fa-//;
            $name = $_;
        } elsif ($i == 5) {
            $unicode = $1 if /\[&amp;#x([0-9a-f]*);\]/;
        } elsif ($i == 0) {
            if (defined $name) {
                print ", \n" if $count++ != 0;
                my $js = "    { name: '$name', code: 0x$unicode";
                $js .= ", ver: '$ver'" if defined $ver;
                $js .= " }";
                print $js;
                ($ch, $name, $unicode, $ver) = (undef, undef, undef, undef);
            }
        } # switch
    } # <>
    print "\n]\n";
    return 0;
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
    print "[$LOGNAME]  \n";
    print "Written by Lenik,  Version 0.$RCSID{rev},  Last updated at $RCSID{date}\n";
}

=head1 SEE ALSO

The L<cmt/"Perl_simple_cli_program_template">

=head1 AUTHOR

Lenik (谢继雷) <lenik (at) bodz.net>

=cut
