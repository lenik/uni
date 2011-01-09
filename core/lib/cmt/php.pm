package cmt::php;

=head1 NAME

cmt::php - simple php functions for dynamic contents support

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::log(2);
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: .pm 764 2007-12-04 14:20:23Z Lenik $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(php_perl
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use cmt::php;
    mysub(arguments...)

=head1 simple php functions for dynamic contents support

B<cmt::php> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::php-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub php_perl {
    local $_ = join('', @_);
    s/<\? (?:php)? ( ( (\\.) | ([^\?]) | (\?[^>]) )* ) \?>/eval($1)/sgex;
    return $_;
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

Xima Lenik <name@mail.box>

=cut
1