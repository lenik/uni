package labat::win32;   # win32 functions

use strict;
use cmt::util;
use labat;
use Data::Dumper;
use Exporter;
use Win32::Registry;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(set_env
                 set_ctxmenu
                 set_assoc
                 set_reg
                 );
our %BTAB   = qw(set_env            STD
                 set_ctxmenu        NIDC
                 set_assoc          DLC
                 set_reg            STD
                 );

sub info;   *info = \*labat::info;
sub info2;  *info2= \*labat::info2;
sub hi;     *hi   = \*labat::hi;

our $HK_ENV;
    $::HKEY_LOCAL_MACHINE->Open(
        'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
        $HK_ENV) or die "Can't open environment key";

# Description - List, Command
sub DLC {
    my $code = shift;
    sub {
        my $ctx = shift;
        my ($dl, $c) = @_;
        my ($d, @l) = split(/\s+/, $dl);
        $c = join(' ', labat::_resolv2($ctx, $c));
        $code->($ctx, $d, \@l, $c)
    }
}

# Name - Id - Description, Command
sub NIDC {
    my $code = shift;
    sub {
        my $ctx = shift;
        my ($nid, $c) = @_;
        my ($n, $i, $d) = split(/\s+/, $nid, 3);
        $c = join(' ', labat::_resolv2($ctx, $c));
        $code->($ctx, $n, $i, $d, $c)
    }
}

sub set_env { &hi;
    my $ctx = shift;
}

sub set_ctxmenu { &hi;
    my $ctx = shift;
}

sub set_assoc { &hi;
    my $ctx = shift;
}

sub set_reg { &hi;
    my $ctx = shift;

}

1