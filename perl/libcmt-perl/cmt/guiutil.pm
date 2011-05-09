package cmt::guiutil;

=head1 NAME

cmt::guiutil - DeScRiPtIoN

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::ftime();
use cmt::ios;
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Data::Dumper;
use Exporter;
use IO::Handle;
use IO::Select;
use Socket;
use Thread;
use Tk;
use Tk::Event;

our @ISA    = qw(Exporter);
our @EXPORT = qw(bgloop
                 mon_fdout
                 );

# INITIALIZORS

sub bgloop;
sub mon_fdout;
sub fdout_readable;

=head1 SYNOPSIS

    use cmt::guiutil;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::guiutil> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::guiutil-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub bgloop {
    my $widget      = shift;
    my $interval    = shift;        # ms
    my $callback    = shift;
    my @args        = @_;
    my $timeout     = $interval;    # ms
    my $wrapper;
       $wrapper = sub { *__ANON__ = '<bgloop-wrapper>';
        my $ret = $callback->(@args);

        # return undef to terminate
        return unless defined $ret;

        # return 1 to continue immediately
        if ($ret > 0) {
            $timeout = $interval;

        # return -1 to slowdown
        } else {
            if ($timeout <= 0) {
                $timeout = 1;       # to avoid multiply with 0, or negatives.
            } else {
                $timeout = 2 * $timeout;
            }
        }

        # register for the next call
        $widget->after($timeout, $wrapper);
    };

    # Don't write "$wrapper->(); " as the initial timeout may be very big.
    $widget->after($timeout, $wrapper);
}

sub mon_fdout {
    my ($fd, $mw, %cfg) = @_;

    my $exit_on     = 'hide';

    my $mystatus    = $cfg{-status} || 'No Status';
    my $myinfo      = $cfg{-info}   || 'No Info';
    my $statusvar   = $cfg{-statusvar} || \$mystatus;
    my $infovar     = $cfg{-infovar}   || \$myinfo;

    my $w           = $mw ? $mw->Toplevel : new MainWindow;
                      $mw = $w unless $mw;
    $w->configure(-title => $cfg{-title} || 'mon_fdout',
                  );
    my $width       = $cfg{-width} || 480;
    my $height      = $cfg{-height}|| 320;
    $w->geometry($width.'x'.$height);

    my $body        = $w->Scrolled('Text', -width=>5, -state=>'normal', -height=>5, -relief=>'sunken', -scrollbars=>'se', -wrap=>'none',
                                   # -exportselection=>'1',
                                   )->pack(-fill=>'both', -expand=>1);
    my $finfo       = $w->Frame(-relief=>'groove')->pack(-fill=>'x');
    my $labstatus   = $finfo->Label(-justify=>'left', -relief=>'flat',
                                    -textvariable=>$statusvar)->pack(-fill=>'x', -ipadx=>4, -side=>'left', -padx=>4);
    my $labinfo     = $finfo->Label(-justify=>'left', -relief=>'flat',
                                    -textvariable=>$infovar  )->pack(-ipadx=>4, -side=>'right', -padx=>4);
    my $fctrl       = $w->Frame(-relief=>'flat')->pack(-side=>'bottom');
    my $btnok       = $fctrl->Button(-underline=>0, -overrelief=>'raised',
                                     -state=>'disabled', -relief=>'raised', -text=>'OK',     -compound=>'left', -bitmap=>'warning',-padx=>4)->pack(-ipadx=>4, -side=>'left', -padx=>4);
    my $btncancel   = $fctrl->Button(-underline=>0, -overrelief=>'raised',
                                     -state=>$cfg{-cancel}?'normal':'disabled', -relief=>'raised', -text=>'Cancel', -compound=>'left', -bitmap=>'error',  -padx=>4)->pack(-ipadx=>4, -side=>'left', -padx=>4);
    my $btnhide     = $fctrl->Button(-underline=>0, -overrelief=>'raised',
                                     -state=>$cfg{-hide}?'normal':'disabled', -relief=>'raised', -text=>'Hide',   -compound=>'left', -bitmap=>'gray12', -padx=>4)->pack(-ipadx=>4, -side=>'left', -padx=>4);
    my $btnoptions  = $fctrl->Button(-underline=>0, -overrelief=>'raised',
                                     -state=>'normal', -relief=>'raised', -text=>'Options',-compound=>'left', -bitmap=>'info',   -padx=>4)->pack(-ipadx=>4, -side=>'left', -padx=>4);

    my %ui = (mw        => $mw,
              w         => $w,
              body      => $body,
              finfo     => $finfo,
              labstatus => $labstatus,
              labinfo   => $labinfo,
              btnok     => $btnok,
              btncancel => $btncancel,
              btnhide   => $btnhide,
              btnoptions=> $btnoptions,
              statusvar => $statusvar,
              infovar   => $infovar,
              blocklines=> 0,
              line_no   => 0,
              );

    $btnok->configure(-command => sub { *__ANON__ = '<btnok>';
        $exit_on = 'ok';
        $w->destroy;
    });

    $btncancel->configure(-command => sub { *__ANON__ = '<btncancel>';
        $exit_on = 'cancel';
        $w->destroy;
    });

    $btnhide->configure(-command => sub { *__ANON__ = '<btnhide>';
        $exit_on = 'hide';
        $w->destroy;                    # hide function?..
    });

    # Tk::fileevent doesn't work in win32
    # (Because the select() method only implemented in sockets)
    #
    # $mw->fileevent($fd, 'readable' => [\&read_proc, $fd, \%cfg, \%ui]);

    my ($fdout_rd, $fdout_wt);
    socketpair($fdout_rd, $fdout_wt, AF_UNIX, SOCK_STREAM, PF_UNSPEC)
        or die("Can't make socketpair");
    $fdout_wt->autoflush(1);

    my $eventmgr = $ui{eventmgr} = $mw;

    my $eventmode = $cfg{-eventmode} || 'fileevent';

    if ($eventmode eq 'fileevent') {
        $eventmgr->fileevent($fdout_rd, 'readable' =>
                             # sub {print "read!\n";},
                             [\&fdout_readable, $fdout_rd, \%cfg, \%ui],
                             );
    } elsif ($eventmode =~ /^auto$/) {
        my $ios = new cmt::ios(
            readout => [ $fdout_rd ],
            -read   =>
                sub { *__ANON__ = '<read>';
                    my ($ctx, $fd) = @_;
                    # assert $fd == $fdout_rd;
                    my $eof = ! fdout_readable($fd, \%cfg, \%ui);
                    if ($eof) {
                        $ctx->exit;
                    }
                    return 1;       # never slowdown.
                },
            -write  => sub {  *__ANON__ = '<write>'; undef },
            -err    => sub {  *__ANON__ = '<err>'; undef },
        );

        my $ctx = $ios->create_context('readout');

        my $bgcall = sub { *__ANON__ = '<bgcall>';
            my $cont = $ctx->iterate();
            if ($cont) {
                return 1;           # next immediately
            } else {
                return undef;       # break bgloop
            }
        };

        bgloop $eventmgr, 0, $bgcall;
    } else {
        die("Invalid eventmode: $eventmode");
    }

    # my $child = fork;
    my $child = new Thread(sub { *__ANON__ = '<child>';
        my $slowdown = $cfg{-slowdown};
        my $srcfilter = $cfg{-srcfilter};
        while (<$fd>) {
            if ($srcfilter) {
                $_ = $srcfilter->($_);
                next unless defined $_;
            }
            _log2 "Send: $_";
            print $fdout_wt $_;
            fsleep $slowdown if $slowdown;
        }
        shutdown $fdout_wt, 2;
    });

    _log2 "MainLoop";
    if ($mw) {
        $w->grab;
        $w->waitWindow;
    } else {
        MainLoop;
    }

    _log2 "exit on $exit_on";
    if ($exit_on eq 'ok') {
        # Block, the btnok only enabled after EOF
        $child->join;                   # Block
    } elsif ($exit_on eq 'hide') {
        # Block
        $child->join;                   # ?? ...
    } elsif ($exit_on eq 'cancel') {
        # Non-Block, and killed automatic after program exit
        $child->detach;                 # Kill instead? ...
    } else {
        die("Invalid exit_on ($exit_on)");
    }

    # The caller should close($fd).
}

sub fdout_readable {
    _log2 "read";
    my ($fdout_rd, $cfg, $ui) = @_;
    my $eventmode = $cfg->{-eventmode};
    my $out = <$fdout_rd>;
    my $eof = 0;
    unless (defined $out) {
        $eof = 1;
        $out = $cfg->{-endtext} || '';
    }

    $out =~ s/\r//g;                    # improve display: don't show "\x{d}"

    my $body = $ui->{body};
    _log2 "read $out";
    $body->insert('end', $out);

    if (my $maxlines = $cfg->{maxlines} || 100) {
        my $lines = int($body->index('end'));
        $body->delete('1.0', ($lines-$maxlines).'.0') if ($lines > $maxlines);
    }

    if ($eof) {
        _log2 "read: EOF";
        if ($eventmode eq 'fileevent') {
            my $eventmgr = $ui->{eventmgr};
            $eventmgr->fileevent($fdout_rd, 'readable' => undef);
        }
        shutdown $fdout_rd, 2;

        # enabled ok button
        $ui->{btnok}->configure(-state => 'active');
    }

    $ui->{line_no}++;
    if ($ui->{blocklines}-- < 0) {
        $body->yview('end');
        $ui->{blocklines} = $cfg->{blocklines};
        if ($cfg->{-displayinfo}) {
            ${$ui->{infovar}} = 'Line: '.$ui->{line_no};
        }
        $ui->{w}->idletasks;
    }

    if ($eof && $cfg->{-autoclose}) {
        my $autoclose = $cfg->{-autoclose};
        $ui->{eventmgr}->after(1000 * $autoclose, sub { $ui->{w}->destroy });
    }
    return ! $eof;
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