package MDA::Form::NSIS;

=head1 NAME

MDA::Form::NSIS - .ini Form parameters file [Nsis]

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::log(2);
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: .pm 756 2007-11-27 12:51:15Z Lenik $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(static_method
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use MDA::Form::NSIS;
    static_method(arguments...)

=head1 .ini Form parameters file [Nsis]

B<MDA::Form::NSIS> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-MDA::Form::NSIS-RESOLVES.

=head1 METHODS

=cut
=head2 new()

=cut
sub new {
    my $class           = shift;
    my $this            = bless {}, $class;
    $this->{method}     = \&method;
    $this->{attribute}  = undef;
    return $this;
}

=head2 method()

=cut
sub method {
    my $this = shift;
    # TODO
}

=head1 STATIC METHODS

=cut
=head2 static_method(arguments)

=cut
sub static_method {
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

Xima Lenik <name@mail.box>

=cut
1