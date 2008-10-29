#!/usr/bin/perl

=head1 NAME

mcdir - make magic directories

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    $LOGNAME    = 'mcdir'; # $0 =~ /([^\/\\.]+)(?:\.\w+)*$/;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
use Getopt::Long;

sub _main; sub _version; sub _help;

our $opt_alphabet   = '0123456789';
our $opt_length     = 2;
our $opt_pad_char;
our $opt_depth      = 2;

sub _boot {
    GetOptions('quiet|q'        => sub { $LOGLEVEL-- },
               'verbose|v'      => sub { $LOGLEVEL++ },
               'version'        => sub { _version; exit 0 },
               'help|h'         => sub { _help; exit 0 },
               'alphabet|a=s',
               'length|l=n',
               'pad-char|p:s',
               'depth|d=n',
               );
    if (defined $opt_pad_char and $opt_pad_char eq '') {
        $opt_pad_char = substr($opt_alphabet, 0, 1);
    }

    die "alphabet is empty" if $opt_alphabet eq '';
    $opt_alphabet = [ split('', $opt_alphabet) ];

    _main;
}

=head1 SYNOPSIS

B<mcdir>
    S<[ B<-q> | B<--quiet> ]>
    S<[ B<-v> | B<--verbose> ]>
    S<[ B<-h> | B<--help> ]>
    S<[ B<--version> ]>
    S<[ B<--> ]>
    S<[ I<...the rest of arguments...> ]>

=head1 DESCRIPTION

B<mcdir> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-mcdir-RESOLVES.

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
    print "\nSyntax: \n    $0 [OPTION] [--] ...\n", <<'EOM';

Common options:
    -a, --alphabet=CHARS    characters in generated names
    -l, --length=LENGTH     max length of name
    -p, --pad-char[=CHAR]   default CHAR is the first char in alphabet
    -d, --depth=DEPTH       max depth of directories
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
    -h, --help              show this help page
        --version           print the version info
EOM
}

exit (_boot or 0);

sub _main {
    iterate 0, '.', 0, '';
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)
sub iterate {
    my ($level, $dpre, $pos, $npre) = @_;
    my $name = '';
    if ($pos < $opt_length) {
        for (@$opt_alphabet) {
            my $$name . $_

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
    print "[$LOGNAME] make magic directories \n";
    print "Written by Lenik,  Version 0.$RCSID{rev},  Last updated at $RCSID{date}\n";
}

=head1 SEE ALSO

The L<cmt/"Perl_simple_cli_program_template">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
