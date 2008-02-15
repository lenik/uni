package labat::win32;

=head1 NAME

labat::win32 - Win32 Functions For Labat

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL %ROOT);
# use cmt::codec;
use cmt::lang('_o', '_or');
use cmt::log(2);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = $labat::LOGLEVEL;
use cmt::util('qsplit');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use labat;
use Data::Dumper;
use Exporter;
use Win32::TieRegistry(':REG_', Delimiter => '/', TiedHash => \%ROOT);

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

sub hi;     *hi     = *labat::hi;

our $CROOT          = $ROOT{'Classes'};
our $USR_ENV        = $ROOT{'CUser/Environment'};
our $SS_MGR         = $ROOT{'LMachine/SYSTEM/CurrentControlSet/Control'}
                               ->{'Session Manager'};
our $SYS_ENV        = $SS_MGR->{'Environment'};

=head1 SYNOPSIS

    use win32;
    mysub(arguments...)

=head1 Win32 Functions For Labat

B<win32> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-win32-RESOLVES.

=head1 FUNCTIONS

=cut

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
        my ($n, $i, $d) = qsplit(qr/\s+/, $nid, 3, );
        $c = join(' ', labat::_resolv2($ctx, $c));
        for (split(/\s+/, $n)) {
            $code->($ctx, $_, $i, $d, $c)
        }
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
        my $SLASH = $st eq 'w' ? '\\' : '/';
            $val =~ s/[\/\\]/$SLASH/g;
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
                @new = grep { my $t = substr($_, 0, $len); $t =~ s/[\/\\]/$SLASH/g;
                              $t = lc $t unless $case; $val ne $t } @old;
                # _log2 "[$val] ", join(',',@old), " -> ", join(',', @new);
            }
            $new = join($IFS, @new);
        } elsif ($op =~ /^(?:\+([\^\$]?)|([\^\$])\+)$/) {
            # prepend/append
            if (_o($1) eq '^' or _o($2) eq '^') {
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
    my $k = ref $_[0] ? shift : undef;
    my $path = shift;
    unless (defined $k) {
        $path =~ s|^/?(\w+)/||;
        my $start = $1;
        $k = $ROOT{$start};
    }
    $k->CreateKey($path)
}

sub set_ctxmenu { &hi;
    my ($ctx, $nam, $id, $desc, $cmd) = @_;
    if ($nam =~ /^\./) {
        my $alias = _or($CROOT->{"$nam//"}, '');
        $alias =~ s/\s+$//s;
        if ($alias eq '') {
            $alias = 'lapiota.file'.$nam;
            _automk $CROOT, "$nam/";
            $CROOT->{"$nam//"} = $alias;
        }
        $nam = $alias;
    }
    _automk $CROOT, "$nam/Shell/$id/Command";
    $CROOT->{$nam}->{'Shell'}->{$id} = {
        '/' => _or($desc, $id),
        'Command//' => $cmd,
    };
}

sub set_assoc { &hi;
    my ($ctx, $progid, $exts, $cmd) = @_;
    for my $ext (@$exts) {
        $CROOT->{$ext}->{'/'} = $progid;
    }
    my $h = _automk $CROOT, "$progid/Shell/Open/Command";
    $h->{'/'} = $cmd;
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
    my $vname = '/';
       $vname = '/'.$1 if $key =~ s/\@(.*?)$//;
    my $h = _automk $key;
    $h->{$vname} = [ $val, $vt ];
}

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