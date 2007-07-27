@rem = '
    @echo off

        if not "%OS%"=="Windows_NT" goto err_os

        perl %~dpnx0 %*
        goto end

    :err_os
	echo You must run this program under Windows NT/2000 or above.
	goto end
    ';

#!/usr/bin/perl

use strict;
use cmt::util;
use cmt::vcs;
use Getopt::Long;

sub boot;
sub main;
sub info;
sub info2;
sub version;
sub help;

our $opt_verbtitle      = 'hex';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our $opt_table          = '0123456789abcdefghijklmnopqrstuvwxyz';
our $opt_upper          = 0;
our $opt_force          = 0;
our $opt_from           = 0;
our $opt_to             = 0;
our $opt_print          = '<report>';       # default is table-report
our $opt_width          = 1;
our $opt_ccr_mode       = 0;
our $opt_dump;

# Control-Code Representation
our @ccr_table_n2c  = (
        '.', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',     # 00
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '[', '\\',']', '^', '_',     # 10
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # 20
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # 30
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # 40
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # 50
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # 60
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # 70
        ' ', '!', '"', '#', '$', '%', '&', '!', '(', ')', '*', '+', ',', '-', ' ', '/',     # 80
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',     # 90
        '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',     # A0
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', ' ', ' ', ' ', ' ',     # B0
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # C0
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # D0
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # E0
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',     # F0
        );

our @ccr_table_c2n  = (
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # 00
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # 10
       128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141,   0, 143,      # 20
       144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159,      # 30
       160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175,      # 40
       176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191,      # 50
        -1,   1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12,  13,  14,  15,      # 60
        16,  17,  18,  19,  20,  21,  22,  23,  24,  25,  26,  27,  28,  29,  30,  31,      # 70
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # 80
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # 90
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # A0
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # B0
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # C0
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # D0
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # E0
        -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,      # F0
        );

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               'alphabet=s' => \$opt_table,
               'uppercase'  => sub { $opt_upper = 1 },
               'lowercase'  => sub { $opt_upper = 0 },
               'from|f=n',
               'to=n',
               'print:s',
               'width=n',
               'ccr-mode',
               'force',
               'dump:n',
               );
    $opt_table = uc $opt_table if $opt_upper;
    $opt_table = lc $opt_table if not $opt_upper;
    $opt_print = '$dh' if $opt_print eq '';

    $opt_dump ||= 16 if defined $opt_dump;
    main;
}

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub version {
    my %id = parse_id('$Id: hex.bat,v 1.3 2007-07-27 13:35:56 lenik Exp $');
    print "[$opt_verbtitle] Number system reference \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        $0 <options> ...

Options:
        --alphabet=<string>         (a)
        --ccr-mode                  (c)
        --dump=<line-width>         (d, set -d to have a 16-byte width)
        --force
        --from=<from-system>        (f)
        --lowercase                 (l)
        --print=<format>            (p, default '\$dh'>
        --to=<target-system>        (t)
        --uppercase                 (u)
        --width=<width-in-letters>  (w)
        --quiet                     (q)
        --verbose                   (v, repeat to get more info)
        --version
        --help                      (h)

Format-String: *
        \$<runtime-variable>
        \\<escape-char>
        <literal-char>

Example:
    hex -p=\$dh. 233 12 55 302 34           Output: e9.c.37.12e.22.
    hex 233 12 55 302 34 -p                 Output: e9c3712e22
    hex -c -p=\$dd. '.brp'                   Output:
EOM
}

exit boot;

sub systemDecode {
    my ($text, $sys) = @_;
    my @chars = split('', $text);
    my $num = 0;
    for (@chars) {
        my $order = index($opt_table, $_);
            die "Invalid digit: $_" if ($order < 0 and !$opt_force);
        $num = $num * $sys + $order;
    }
    return $num;
}

sub systemEncode {
    my ($num, $sys) = @_;
    my $text = '';
    my $zero = substr($opt_table, 0, 1);
    my $len = 0;

    while ($num > 0) {
        $text = substr($opt_table, $num % $sys, 1) . $text;
        $num = int($num / $sys);
        $len++;
    }
    $text = $zero x ($opt_width-$len) . $text if ($len < $opt_width);
    return $text;
}

sub radixTable {
    local $_ = $_;
    $_ = shift if @_;

    my $num_b = systemDecode($_, 2);
    my $num_o = systemDecode($_, 8);
    my $num_d = systemDecode($_, 10);
    my $num_h = systemDecode($_, 16);

    my $bb = systemEncode($num_b, 2);
    my $bo = systemEncode($num_b, 8);
    my $bd = systemEncode($num_b, 10);
    my $bh = systemEncode($num_b, 16);

    my $ob = systemEncode($num_o, 2);
    my $oo = systemEncode($num_o, 8);
    my $od = systemEncode($num_o, 10);
    my $oh = systemEncode($num_o, 16);

    my $db = systemEncode($num_d, 2);
    my $do = systemEncode($num_d, 8);
    my $dd = systemEncode($num_d, 10);
    my $dh = systemEncode($num_d, 16);

    my $hb = systemEncode($num_h, 2);
    my $ho = systemEncode($num_h, 8);
    my $hd = systemEncode($num_h, 10);
    my $hh = systemEncode($num_h, 16);

    if ($opt_print eq '<report>') {
        format =
Number @<
       $_
    From  \  To |    Binary    |    Octal     |   Decimal    | Hexadecimal
    ------------+--------------+--------------+--------------+--------------
    Binary      |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>>
                 $bb            $bo            $bd            $bh
    Octal       |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>>
                 $ob            $oo            $od            $oh
    Decimal     |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>>
                 $db            $do            $dd            $dh
    Hexadecimal |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>> |@>>>>>>>>>>>>
                 $hb            $ho            $hd            $hh

.
        write;
    } else {
        print eval("\"$opt_print\"");
    }
}

sub main {
    if (defined $opt_dump) {
        die "Only hex-dump is supported, yet" if $opt_dump != 16;
        my $s = join(' ', @ARGV);
        $s = readfile $s if -f $s;
        for (my $i = 0; $i < length($s); $i++) {
            my $c = substr($s, $i, 1);
            printf("%02x", ord $c);
        }
        print "\n";
        exit;
    }

    for (@ARGV) {
        if (m/^[\'\"]/) {
            my @chars = split('', substr($_, 1, -1));
            for (@chars) {
                my $num = ord;
                if ($opt_ccr_mode) {
                    $num = $ccr_table_c2n[$num];
                    die "Undefined CCR character: $_" if ($num < 0);
                }
                radixTable $num;
            }
        } else {
            die "Only positive integer are supported. " if (m/^\s*-/);
            radixTable;
        }
    }
}

__END__
:end
