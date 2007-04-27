package cmt::guiutil;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;
use cmt::ftime;
use Socket;
use Thread;
use IO::Handle;
use IO::Select;
use Tk;
use Tk::Event;

sub fdout_readable;

sub info {
    my $msg = shift;
    print "  -- $msg\n";
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

    my %ui = (mw => $mw,
              w => $w,
              body => $body,
              finfo => $finfo,
              labstatus => $labstatus,
              labinfo => $labinfo,
              btnok => $btnok,
              btncancel => $btncancel,
              btnhide => $btnhide,
              btnoptions => $btnoptions,
              statusvar => $statusvar,
              infovar => $infovar,
              refresh => 0,
              line_no => 0,
              );

    $btnok->configure(-command => sub {
        $exit_on = 'ok';
        $w->destroy;
    });

    $btncancel->configure(-command => sub {
        $exit_on = 'cancel';
        $w->destroy;
    });

    $btnhide->configure(-command => sub {
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
    } elsif ($eventmode eq 'idle' || $eventmode =~ /^\d+$/) {
        my $idletask;
        my $select = new IO::Select($fdout_rd);
        my $intv = $eventmode =~ /^\d+$/ ? 1*$eventmode : 0;
        my $cancel_id;
        my $slowdown = $cfg{-slowdown} || 0.1;
        my $timeout = $cfg{-timeout} || 1 * $slowdown;
        my $refresh = $cfg{-refresh} || 10000;
        $idletask = sub {
            my $done;
            my $cc = 0;                 # continuous_select counter
            while (my @ready = $select->can_read($timeout)) {
                my $rh = shift @ready;
                # assert $rh == $fdout_rd
                $done = !fdout_readable($fdout_rd, \%cfg, \%ui);
                $cc++;
                last if $done;
                last if $cc > $refresh;
            }
            # $body->insert('end', "-- cont $cc\n");
            # info "done!" if $done;
            if ($eventmode eq 'idle') {
                $eventmgr->afterIdle($idletask) unless $done;
            } else {
                # $cancel_id->cancel if $done;
                $eventmgr->after($intv, $idletask) unless $done;
            }
        };
        if ($intv) {
            $cancel_id = $eventmgr->afterIdle($idletask);
        } else {
            #$cancel_id = $eventmgr->repeat($intv, $idletask);
            $cancel_id = $eventmgr->after($intv, $idletask);
        }
    } else {
        die("Invalid eventmode: $eventmode");
    }

    # my $child = fork;
    my $child = new Thread(sub {
        my $slowdown = $cfg{-slowdown};
        my $process = $cfg{-process};
        while (<$fd>) {
            if ($process) {
                $_ = &$process($_);
                next unless defined $_;
            }
            # info "Send: $_";
            print $fdout_wt $_;
            fsleep $slowdown if $slowdown;
        }
        shutdown $fdout_wt, 2;
    });

    # info "MainLoop";
    if ($mw) {
        $w->grab;
        $w->waitWindow;
    } else {
        MainLoop;
    }

    # info "exit on $exit_on";
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
    # info "read";
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
    # info "read $out";
    $body->insert('end', $out);

    if (my $maxlines = $cfg->{maxlines} || 100) {
        my $lines = int($body->index('end'));
        $body->delete('1.0', ($lines-$maxlines).'.0') if ($lines > $maxlines);
    }

    if ($eof) {
        # info "read: EOF";
        if ($eventmode eq 'fileevent') {
            my $eventmgr = $ui->{eventmgr};
            $eventmgr->fileevent($fdout_rd, 'readable' => undef);
        }
        shutdown $fdout_rd, 2;

        # enabled ok button
        $ui->{btnok}->configure(-state => 'active');
    }

    $ui->{line_no}++;
    if ($ui->{refresh}-- < 0) {
        $body->yview('end');
        $ui->{refresh} = $cfg->{refresh};
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

@ISA = qw(Exporter);
@EXPORT = qw(
	mon_fdout
	);

1;
