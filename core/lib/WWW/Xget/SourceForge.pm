package WWW::Xget::SourceForge;

=head1 NAME

WWW::Xget::SourceForge - SourceForge Xget driver

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 0;
use cmt::log(2);
use cmt::util('addopts_long');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: SourceForge.pm,v 1.1 2007-09-29 10:33:09 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use WWW::Xget;

our @ISA    = qw(WWW::Xget::Driver);
our @EXPORT = qw();

# INITIALIZORS

=head1 SYNOPSIS

    use UnKnOwN;
    mysub(arguments...)

=head1 DESCRIPTION

B<UnKnOwN> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-UnKnOwN-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 new()

=cut
sub new {
    my $package = shift;
    my $this = {
        'OPTIONS' => [
            'source|s',
            'user|u=s',
            'password|p=s',
            'all-packages|ap',
            'all-versions|av',
        ],
        'HELP' => help(),
        'VERSION' => '0.'.$RCSID{'rev'},
    };
    addopts_long $this->{'OPTIONS'}, $this;
    bless $this, $package;
    $this
}

sub help {
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