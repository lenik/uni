package WWW::Xget;

=head1 NAME

WWW::Xget - Download softwares from specific WWW server

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 0;
use cmt::log(2);
use cmt::util;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(load_driver
                 );

our %ALIAS;

=head1 SYNOPSIS

    use WWW::Xget;

    my $driver = load_driver('DRIVER');
    $driver->{OPT} = VALUE;

    chdir(LOCAL-PATH);
    $driver->do_get(NAMES);

=head1 DESCRIPTION

B<WWW::Xget> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-WWW::Xget-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 load_driver(driver-name)

=cut
sub load_driver {
    my $name = shift;
    if (index($name, '::') == -1) {
        $name = __PACKAGE__.'::'.$name;
    }
    eval "use $name; 1"
        or die "failed to load $name: $@";
    return $name->new();
}

{
    package WWW::Xget::Driver;

    sub do_get      { die "not implemented" }
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