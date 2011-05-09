#!/usr/bin/perl -w

use strict;
use cmt::guiutil;

use Tk;
use Thread;

my $cmd = 'C:\Oracle\ora92\bin\exp.exe'
       .' ctais2/admin@n183 parfile=c:\expm.par 2>&1';

my $txt = ($ENV{OS} ? 'c:' : '/mnt/q_c/') .
    #'/windows/win.ini'
     '/t/0/dnad.pl'
    ;

my ($fd, $pid);

open($fd, "<$txt");
# $pid = open($fd, "c:/src.bat|");

die("Can't open") unless ($fd);

# using .03, 200, 300 for debug purpose
# using 0, 100, 100 for general purpose
mon_fdout($fd, undef,
    -slowdown=>.03,
    -eventmode=>300,
    -refresh => 300,
    #-timeout => 0.,#0.0001,
    );

close $fd;
if ($pid) {
    waitpid $pid, 0;
}
