package labat::win32;

=head1 NAME

labat::win32 - Win32 Functions For Labat

=head1 DESCRIPTION

Support for:

=over

=item Registry

=back

=head1 REFERENCE

=cut

use strict;
# use cmt::codec;
use cmt::util;
use labat;
use Data::Dumper;
use Exporter;
use Win32::TieRegistry(':REG_', Delimiter => '/');

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(set_env
                 set_ctxmenu
                 set_assoc
                 set_reg
                 );
our %CTAB   = qw(set_env            STD
                 set_ctxmenu        NIDC
                 set_assoc          DLC
                 set_reg            STD
                 );

sub info;   *info   = *labat::info;
sub info2;  *info2  = *labat::info2;
sub hi;     *hi     = *labat::hi;

our $SS_MGR         = $Registry->{'LMachine/SYSTEM/CurrentControlSet/Control'}
                               ->{'Session Manager'};
our $USR_ENV        = $Registry->{'CUser/Environment'};
our $SYS_ENV        = $SS_MGR->{'Environment'};
our $CROOT          = $Registry->{'Classes'};

# Description - List, Command
sub _cs_DLC {
    my $code = shift;
    sub { *__ANON__ = '<_cs_DLC>';
        my $ctx = shift;
        my ($dl, $c) = @_;
        my ($d, @l) = split(/\s+/, $dl);
        $c = join(' ', labat::_resolv2($ctx, $c));
        $code->($ctx, $d, \@l, $c)
    }
}

# Name - Id - Description, Command
sub _cs_NIDC {
    my $code = shift;
    sub { *__ANON__ = '<_cs_NIDC>';
        my $ctx = shift;
        my ($nid, $c) = @_;
        my ($n, $i, $d) = split(/\s+/, $nid, 3);
        $c = join(' ', labat::_resolv2($ctx, $c));
        $code->($ctx, $n, $i, $d, $c)
    }
}

=head2 set_env(OP[~][%], ENV, VALUE)

If `~' is specified, then only current user is affected, otherwise all users
are affected.

If '%' is specified, the VALUE is REG_EXPAND_SZ.

=over

=item basic operators

=over

=item set

set ENV to VALUE

=item unset

delete ENV

=back

=item [muw]- edit-path operators

If OP begins with `m', `u' or `w', then this is an edit-path operator.
The prefix [muw] is used to specify the path-style, as:
    m = mixed
    u = unix
    w = windows
these different styles affects the $IFS, slash char (/ or \), etc.
(See the usage of Cygwin/cygpath utility)

=over

=item -[^] (remove)

remove /^DIR*/

=item ^+ (prepend)

prepend DIR to VAR

=item +[$] (append)

append DIR to VAR

=item +MARK (insert-before)

insert DIR before MARK, prepend MARK to VAR if not existed

=item MARK+ (insert-after)

insert DIR after MARK, append MARK to VAR if not existed

=item & (add-to-set)

the VALUE contains components separated by space, using p& operator to
add each component to ENV only if that component isn't existed before.

=back

See also L</lapiota/lib/shell-functions/path>

=back

=cut
sub set_env { &hi;
    sub _SZ($) { shift }
    sub _XZ($) { [ shift, REG_EXPAND_SZ ] }
    my ($ctx, $op, $nam, $val) = @_;
    splice @_, 0, 3;
    my $ENV =#$op =~ s/\*$// ? $SYS_ENV : $USR_ENV;
              $op =~ s/~$// ? $USR_ENV : $SYS_ENV;
    my ($old, $oldt) = $ENV->GetValue($nam);
    sub sz($); *sz = $op =~ s/%$// ? *_XZ :
        ($oldt == REG_EXPAND_SZ ? *_XZ : *_SZ);
    if ($op =~ /^(=|set)$/) {
        $ENV->{$nam} = sz $val;
        return 1;
    } elsif ($op =~ /^--|unset/) {
        delete $ENV->{$nam};
        return 1;
    }

    if ($op =~ s/^[wum]//) {
        my $st = $&;                # slash-type
        my $IFS = $st eq 'w' ? ';' : ':';
        my $IDS = $st eq 'w' ? '\\' : '/';
            $val =~ s/[\/\\]/$IDS/g;
        my $case = $st eq 'u' ? 1 : 0;
        my @old = qsplit qr/$IFS/, $old;

        my (@new, $new);
        if ($op =~ s/^=//) {            # set
            $new = $val;
        } elsif ($op =~ s/^-//) {       # remove
            if ($op =~ s/^\///) {       # regex-remove
                $val .= 'i' unless $case;
                my $rx = eval 'qr'.$val;
                die "can't compile regex `$val': $@" if $@;
                @new = grep { $_ !~ $rx } @old;
            } else {                    # prefix-remove
                $val = lc $val unless $case;
                my $len = length $val;
                @new = grep { my $t = substr($_, 0, $len); $t =~ s/[\/\\]/$IDS/g;
                              $t = lc $t unless $case; $val ne $t } @old;
            }
            $new = join($IFS, @new);
        } elsif ($op =~ /^(?:\+([\^\$]?)|([\^\$])\+)$/) {
            # prepend/append
            if ($1 eq '^' or $2 eq '^') {
                $new = $old eq '' ? $val : $val.$IFS.$old;
            } else {
                $new = $old eq '' ? $val : $old.$IFS.$val;
            }
        } elsif ($op =~ s/^&//) {   # add-set
            @new = @old;
            for my $c (@_) {
                my @exists;
                if ($case) {
                    @exists = grep { $_ eq $c } @new
                } else {
                    @exists = grep { (lc $_) eq (lc $c) } @new
                }
                push @new, $c unless @exists;
            }
        } elsif ($op =~ s/(^\+)|(\+$)//) {
            # insert-before/insert-after
            my $before = $1 eq '+';
            my $mark = $op;
            my $markpos;
            for (my $i = 0; $i < @old; $i++) {
                if ($case ? $old[$i] eq $mark : (lc $old[$i]) eq (lc $mark)) {
                    $markpos = $i;
                    last
                }
            }
            if (defined $markpos) {
                @new = @old;
                if ($before) {
                    splice @new, $markpos, 0, $val
                } else {
                    splice @new, $markpos + 1, 0, $val
                }
            } elsif ($before) {
                @new = ($val, $mark, @old)
            } else {
                @new = (@old, $mark, $val)
            }
        } else {
            die "invalid edit-path operator: $op";
        }
        $new = join($IFS, @new) if @new and !defined $new;
        $ENV->{$nam} = sz $new;
    } else {
        die "invalid operator: $op";
    }
    return 1;
}

sub _automk {
    my $k = ref $_[0] ? shift : $Registry->{'/'};
    my $path = shift;
    $k = $k->{$_} = {} for (split '/', $path);
}

sub set_ctxmenu { &hi;
    my ($ctx, $nam, $id, $desc, $cmd) = @_;
    _automk $CROOT, "$nam/Shell/$id/Command";
    $CROOT->{$nam}->{'Shell'}->{$id} = {
        '/' => $desc,
        'Command//' => $cmd,
    };
}

sub set_assoc { &hi;
    my ($ctx, $progid, $exts, $cmd) = @_;
    for my $ext (@$exts) {
        $CROOT->{$ext}->{'/'} = $progid;
    }
    _automk $CROOT, "$progid/Shell/Open/Command";
    $CROOT->{$progid}->{'Shell'}->{'Open'} = {
        'Command//' => $cmd
    };
}

sub set_reg { &hi;
    my ($ctx, $key, $val) = @_;
    my $vt;
    if ($val =~ s/^([sx])z://i) {
        $vt = lc($1) eq 's' ? REG_SZ : REG_EXPAND_SZ;
    } elsif ($val =~ s/^bin://i) {
        $vt = REG_BINARY;
        $val = pack 'H'.length($val), $val;
    } else {
        $vt = REG_DWORD;
        $val = eval($val) + 0;
        $val = pack 'I1', $val;
    }
    my $h = $Registry->{'/'};
    my $vname = '/';
    for (split('/', $key)) {
        if (s/^\@//) {
            $vname = '/'.$_;
            last;
        }
        $h = $h->{$_} = {};
    }
    $h->{$vname} = [ $val, $vt ];
}

1