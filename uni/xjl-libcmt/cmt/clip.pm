package cmt::clip;

=head1 NAME

cmt::clip - Clipboard function for varies systems

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: .pm 1022 2008-10-29 11:51:30Z lenik $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(getclip
                 setclip
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use cmt::clip;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::clip> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::clip-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub getclip_x {
    open(X, 'xsel -p |') or die "Can't read from xsel -p";
    my @lines;
    while (<X>) {
        push @lines, $_;
    }
    close X;
    join('', @lines);
}

sub getclip_win32 {
}

sub setclip_x {
    open(X, '| xsel -ipb') or die "Can't write to xsel -ipb";
    for (@_) {
        print X $_;
    }
    close X;
}

sub getclip {
    getclip_x;
}

sub setclip {
    &setclip_x;
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
