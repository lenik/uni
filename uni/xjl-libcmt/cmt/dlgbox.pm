package cmt::dlgbox;

=head1 NAME

cmt::dlgbox - Common Dialog Boxes

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
use Tk;
use Tk::Event;

our @ISA    = qw(Exporter);
our @EXPORT = qw(choice
                 prompt
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use cmt::dlgbox;
    mysub(arguments...)

=head1 Common Dialog Boxes

B<cmt::dlgbox> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::dlgbox-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub choice {
}

sub prompt {
    my ($title, $val, $mw, %cfg) = @_;
    my $w = $mw ? $mw->Toplevel : new MainWindow;
    $w->configure(-title => $title);
    my $width       = $cfg{-width} || 320;
    my $height      = $cfg{-height}|| 100;
    $w->geometry($width.'x'.$height);

    my $labtitle    = $w->Label(-justify => 'center', -relief => 'flat',
                                -text => $title)
                         ->pack(-side => 'top', -fill => 'x', -expand => 1);
    my $input       = $w->Entry(-relief => 'sunken')->pack();
    my $controls    = $w->Frame(-relief => 'flat')->pack();
    my $btncancel   = $w->Button(-overrelief => 'raised', -relief => 'raised',
                                 -compound => 'none', -state => 'normal',
                                 -text => 'ok')->pack(-side => 'right');
    my $btnok       = $w->Button(-overrelief => 'raised', -relief => 'raised',
                                 -compound => 'none', -state => 'normal',
                                 -text => 'cancel')->pack(-side => 'right');
    if ($mw) {
        $w->grab;
        $w->waitWindow;
    } else {
        MainLoop;
    }
    return $val;
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