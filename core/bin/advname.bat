@rem = '
        @echo off
        rem $Id: advname.bat,v 1.2 2004-09-22 08:39:07 dansei Exp $

        if "%1"=="" goto help

        if "%OS%"=="Windows_NT" goto sysnt
        perl -IZ:\t\l\lib z:\T\AdvName.bat %1 %2 %3 %4 %5 %6 %7 %8 %9
        goto end
:sysnt
        perl -I%~d0%~p0..\lib %~f0 %1 %2 %3 %4 %5 %6 %7 %8 %9
        goto end

:help
        echo #######################################################################
        echo ##    ADVanced NAME percolation tool            Version 1.00a        ##
        echo ## ----------------------------------------------------------------- ##
        echo ## advname [-d/--includedir] [-R/-r/--recursive] {-x commands}       ##
        echo ##         regular exp. substitute                                   ##
        echo ##   -d/--includedir    run through including directory name         ##
        echo ##   -R/-r/--recursive  recursive directories                        ##
        echo ##   -x commands ...    execute external commands for each name      ##
        echo ## show help:           -h/--help                                    ##
        echo ## show version:        -v/--version                                 ##
        echo #######################################################################
        echo ## e.g.:   advname s/abcd/abc/ *.*
        echo ##         advname s/.*/\r/ *.*            \r: rand 0..999999
        echo ##         advname s/.*/\s\r/ *.*          \s: serialnum from 1000...
        echo ## others: $SERINUM2: serial num of files from 0...
        goto end
@';

#!/usr/bin/perl

use strict;
use Config;
use Getopt::Long;
use Data::Dumper;

#use Diagnostics;

my $Breaked;

my (%Options, $Dirname, $Subdir, $Basename);
my @Files;

my $space       = " ";
my $linebreak   = "\n";

$SIG{'BREAK'} = "BreakFunc";
$SIG{'INT'} = "BreakFunc";

my $DSEP = ($Config{'osname'} =~ m/nix/) ? '/' : '\\';

GetOptions(\%Options, "x=s@", "d", "-dirname", "b", "-basename", "r", "R", "-recursive");
        ($Options{'d'} || $Options{'-dirname'}) && ($Dirname = 1);
        ($Options{'r'} || $Options{'R'} || $Options{'-recursive'}) && ($Subdir = 1);
        ($Options{'b'} || $Options{'-basename'}) && ($Basename = 1);

my @Commands;
$Options{'x'} && (@Commands = @{$Options{'x'}});

print "@Commands";

my ($rcmd, $rsrc, $rdest, $rmodifier) = split("/", shift @ARGV);
$rsrc =~ s/\\/\\\\/g;
$rdest =~ s/\\/\\\\/g;

grep($_ eq $rcmd, qw(m s tr)) || die "you must specify command as s(substitute), m(match), tr(translate)\n";

map { push @Files, GetFiles(glob($_)) } @ARGV;

#######################################################################
# for using with no external commands,                                #
#       p:      search special filenames                              #
#       s:      rename files                                          #
# for using with external commands                                    #
#       p:      run commands for each searched file                   #
#       s:      run commands for each searched file with the second   #
#               argument of dest. string                              #
#######################################################################

my $SERINUM = 1000;
my $SERINUM2 = 0;
for (@Files) {
        my $eret;
        my ($srcname, $destname) = ($_, $_);

        # 将替换的状态存入$eret
        $rcmd eq 'm' && eval "\$eret = (\$destname =~ m/$rsrc/$rmodifier)";
        $rcmd eq 's' && eval "\$eret = (\$destname =~ s/$rsrc/$rdest/$rmodifier)";
        $rcmd eq 'tr' && eval "\$eret = (\$destname =~ tr/$rsrc/$rdest/$rmodifier)";

        if ($eret) {
                my $RANDNUM;
                # 插入$destname中的控制符

                $RANDNUM = int(rand()*1000000);
                eval "\$destname =~ s/\\\\r/$RANDNUM/$rmodifier";
                eval "\$eret = (\$destname =~ s/\\\\s/$SERINUM/$rmodifier)";
                if ($eret) { $SERINUM++; }

##              eval "\$destname =~ s/\\\"/\"/$rmodifier";

                # my ($srcbase, $destbase) = (basename($srcname, $_));
                if (@Commands) {
                        my $command;
                        foreach $command (@Commands) {
                                $rcmd eq 'm' && callext("$command $srcname");
                                $rcmd eq 's' && callext("$command $srcname $destname");
                                $rcmd eq 'tr' && callext("$command $srcname $destname");
                        }
                } else {
                        $rcmd eq 'm' && _print($srcname);
                        $rcmd eq 's' && _rename($srcname, $destname);
                        $rcmd eq 'tr' && _rename($srcname, $destname);
                } # if @Commands
        } # if $eret
        $SERINUM2++;
} # for

################################## subrotines #########

sub basename {
        if (@_ > 0) {
                local $_;
                my @basenames;
                for (@_) {
                        push @basenames, basename();
                }
                @basenames;
        } else {
                my $pos = rindex($_, $DSEP);
                substr $_, $pos >= 0 ? $pos + 1 : 0;
        }
}

sub _print {
        $Config{'osname'} =~ m/nix/ || (@_ = basename(@_));
        print "@_\n";
}

sub _rename {
        $Config{'osname'} =~ m/nix/ || (@_[1] = basename($_[1]));
        print "rename ".$_[0]." to ".$_[1]."."."\n";
        rename $_[0], $_[1];
}

sub callext {
        $Config{'osname'} =~ m/nix/ || (@_[1, 2] = basename(@_[1, 2]));
        `@_`;
}

sub GetFiles {
        my @RetFiles;
        local $_;
        if ($Breaked) { last; }
        for (@_) {
                if (-f) {
                        push @RetFiles, $_;
                } elsif (-d) {
                        if ($Dirname) { push @RetFiles, $_; }
                        if ($Subdir) { push @RetFiles, GetFiles(glob("$_/*")); }
                }
        }
        @RetFiles;
}

sub BreakFunc {
        print "Breaked!\n";
        $Breaked = 1;
        $SIG{'BREAK'} = undef;
        $SIG{'INT'} = undef;
}

__END__
:end
