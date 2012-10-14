package <?= _file_name >;

=head1 NAME

<?= _file_name > - <?= TEXT >

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(mysub
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use <?= _file_name >;
    mysub(arguments...)

=head1 DESCRIPTION

B<<?= _file_name >> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-<?= _file_name >-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub mysub {
    # TODO
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

<?= _user > <<?= _email_x >>

=cut
1
