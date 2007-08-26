package cmt::win32;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::path;
use cmt::util;
use Exporter;
use Win32::API;
use Win32::API::Struct;
use Win32::Console;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

@ISA    = qw(Exporter);
@EXPORT = qw(API
             ntspawn
             ntkill
             ntwaitpid
             nttestpid
             clear_screen
             );

# API('X Lib::Fun(XXX)')->(...)
# API('Lib:Fun:XXX:X')->(...)
# API('Lib', 'Fun', 'XXX', 'X')->(...)
# API('Lib', 'X Fun(XXX)')->(...)
sub API {
    if ($#_ == 0) {
        my $s = shift;
        if ($s =~ /^(\S+)\s+(\w+)::(\w+)\((\S+)\)$/) {
            @_ = ($2, $3, $4, $1);
        } else {
            @_ = split(':', $s)
        }
        # info 'API<'.join('|', @_).'>';
    }
    my $lib = shift;
    my $proc = Win32::API->new($lib, @_);
    die "Can't get proc: $!" unless defined $proc;
    sub { $proc->Call(@_) }
}

sub ntspawn {
    my $pid;
    # return pid[31..8] core?[7] signal[6..0]
    my $pid_file = temp_path('pid_'.int(rand 10000));
    # info2 "enable pid-file: $pid_file";
    $ENV{'pid_file'} = $pid_file;

    # info "spawn $opt_runprog ($run_count)";
    my $ret = system('spawn', @_);
    die "failed to spawn: signal ".($ret & 127) if $ret & 127;

    # return $ret >> 8;
    die "pid-file $pid_file is expected to be created"
        unless -e $pid_file;
    $pid = readfile($pid_file);
    unlink $pid_file;
    chomp $pid;
    return $pid;
}

sub ntkill {
    my $pid = shift;
    system 'kill '.$pid;
    #my $cnt = kill 'HUP' => $pid;
    #info2 "kill process $pid: $cnt";
}

sub ntwaitpid {
    my ($pid, $timeout) = @_;
    my $ret = system("waitpid -q $pid $timeout");
    die "failed to waitpid: signal ".($ret & 127) if $ret & 127;
    return $ret >> 8;
}

sub nttestpid {
    my $pid = shift;
    return ntwaitpid($pid, 0) != 0;
}

my $CONSOLE = new Win32::Console(STD_OUTPUT_HANDLE);
sub clear_screen {
    my ($x, $y) = $CONSOLE->Cursor();
    my ($bw, $bh) = $CONSOLE->Size();
    my ($x0, $y0, $x1, $y1) = $CONSOLE->Window();
    my ($w, $h) = ($x1 - $x0 + 1, $y1 - $y0 + 1);
    my $scrolls = $y + $h - $bh;
    if (0) {
        print "current-cursor: $x, $y\n";
        print "current-window: $x0, $y0, $w, $h\n";
        print "buffer-size:    $bw x $bh\n";
        print "set-window:     $x0, $y, $x1, ".($y + $h - 1)."\n";
        print "need-to-scrolls:$scrolls\n";
    }
    if ($scrolls > 0) {
        my $attr = $CONSOLE->Attr();
        # $CONSOLE->Scroll(0, $scrolls, $bw - 1, $bh - $scrolls - 1, 0, 0);
        $CONSOLE->Cursor(0, $bh - 1);
        $CONSOLE->Write("\n" x $scrolls);
        $y -= $scrolls;
    }
    $CONSOLE->Window(1, $x0, $y, $x1, $y + $h - 1);
    $CONSOLE->Cursor($x0, $y);
    # print "test".rand(100);
}

1