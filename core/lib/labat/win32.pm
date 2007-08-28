package labat::win32;   # win32 functions

use strict;
use cmt::util;
use Exporter;
use labat;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(set_env
                 add_opentype
                 set_assoc
                 set_reg
                 );
our %BTAB   = qw(set_env            STD
                 set_reg            STD
                 set_assoc          DLC
                 );

sub info;   *info = \*labat::info;
sub info2;  *info2= \*labat::info2;
sub hi;     *hi   = \*labat::hi;

sub set_env { &hi;
    my $ctx = shift;
}

sub add_opentype { &hi;
    my $ctx = shift;
}

# Description - List, Command
sub DLC {
    my $code = shift;
    sub {
        my $ctx = shift;
        my ($dl, $c) = @_;
        my ($d, @l) = split(/\s+/, $dl);
        # $c = expand($c);
        $code->($ctx, $d, \@l, $c)
    }
}

sub set_assoc { &hi;
    my $ctx = shift;
}

sub set_reg { &hi;
    my $ctx = shift;

}

1