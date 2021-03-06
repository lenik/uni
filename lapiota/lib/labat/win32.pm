package labat::win32;

=head1 NAME

labat::win32 - Win32 Functions For Labat

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL %ROOT);
# use cmt::codec;
use cmt::lang('_o', '_or');
use cmt::log(3);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = $labat::LOGLEVEL;
use cmt::path('$SLASH');
use cmt::util('qsplit');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use labat;
use Data::Dumper;
use Exporter;
use Win32::OLE('in', 'with', 'EVENTS');
use Win32::OLE::Const;
use Win32::TieRegistry(':REG_', Delimiter => '/', TiedHash => \%ROOT);

our @ISA    = qw(Exporter);
our @EXPORT = qw(set_env
                 set_ctxmenu
                 set_assoc
                 set_reg
                 set_acl
                 );
our %CTAB   = qw(set_env            STD
                 set_ctxmenu        NIDC
                 set_assoc          DLC
                 set_reg            STD
                 set_acl            RP
                 );

sub hi;     *hi     = *labat::hi;
sub runcmd;

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

# Reparse arguments split by space
sub _cs_RP {
    my $code = shift;
    sub { *__ANON__ = '<_cs_RP>';
        my $ctx = shift;
        my $cat = join('', @_);
        my @args = labat::_resolv2($ctx, $cat);
        $code->($ctx, @args);
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

See also L<$LAM_KALA/lib/sh/path>

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
        my $slash = $st eq 'w' ? '\\' : '/';
            $val =~ s/[\/\\]/$slash/g;
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
                @new = grep { my $t = substr($_, 0, $len); $t =~ s/[\/\\]/$slash/g;
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
        my $eh = _automk $CROOT, "$ext/";
        $eh->{'/'} = $progid;
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

=comment
my $SETACL;
my %SETACL_PROFILES;

BEGIN {
    %SETACL_PROFILES = (
        'nosafe'    => [
            [qw(-actn setowner
                -ownr "n:S-1-5-32-544;s:y")],
            ],
        'inherit'   => [
            [qw(-actn setprot
                -op   "dacl:np;sacl:nc")],
            [qw(-actn setowner
                -ownr "n:S-1-5-32-544;s:y")], # this owner should be got from the container.
            ],
        );
}


sub setacl_err {
    my ($code, $desc) = @_;
    if ($code != $SETACL_CONST{RTN_OK}) {
        my $str = $SETACL->GetResourceString($code);
        my $msg = $SETACL->GetLastAPIErrorMessage();
        die "failed to $desc: $str, OS error: $msg";
    }
}

sub set_acl { &hi;
    my $ctx = shift;
    unless (defined $SETACL) {
        require Win32::OLE;
        $SETACL = Win32::OLE->CreateObject('SetACL.SetACLCtrl.1')
            or die "SetACL.ocx is not registered";
        $SETACL_CONST = Win32::OLE::Const->Load($SETACL)
            or die "Constants could not be loaded from the SetACL type library.";
        Win32::OLE->WithEvents($SETACL, sub { local *__ANON__ = '<SetACL>';
            });

    # ~PROFILE ...
    # -on OBJECT -ot TYPE -actn ACT ...
    # assert @_ > 1;
    my ($profile) = @_;
    my $rec;
    if ($profile =~ /^~(\w+)([\*]*)$/) {
        $profile = $1;
        shift;
        @_ = map { s|/|\\|g; s|\\|\\\\|g; $_ } @_;
        push @_, qw(-rec cont_obj) if $2;
        die "invalid profile name: $profile"
            unless exists $SETACL_PROFILES{$profile};
        my $ops = $SETACL_PROFILES{$profile};
        for (@$ops) {
            my @args = (@$_, qw(-ot file -on), @_);
            runcmd $SETACL, @args;
        }
    } else {
        runcmd $SETACL, @_;
    }
}
=cut

sub runcmd {
    # _log3 join('|', @_);
    _log2 join('|', @_);
    my $method = 'backtick';
    if ($method eq 'system') {
        system @_;
        if ($?) {
            _log1 "failed($?) to run ", join(' ', @_);
        }
    } else {
        my $cmdline = join(' ', map { /\s/ ? '"'.$_.'"' : $_ } @_);
        my $output = `$cmdline 2>&1`;
        _log1 "failed($?) to run $cmdline: $output" if $?;
    }
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