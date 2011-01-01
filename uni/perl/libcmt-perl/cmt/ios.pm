package cmt::ios;

=head1 NAME

cmt::ios - I/O Stream

=cut
use strict;
use constant RR => 1;
use constant WW => 2;
use constant EE => 4;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::ftime;
use cmt::log(2);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::util;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Data::Dumper;
use Exporter;
use IO::Select;
use List::Util('min', 'max');

our @ISA    = qw(Exporter);
our @EXPORT = qw(mysub
                 );

sub count_undefs;
sub zero_to_undef;
sub double(\$);

# INITIALIZORS

=head1 SYNOPSIS

    use cmt::ios;

    my $ios = new ioevt(
        group1  => [ $fd1, $fd2, ... ],
        group2  => ...,
        -read   => sub { shift; $buf=<shift>; return $wbuf },
        -write  => sub { shift; print <shift> $wbuf; },
        -err    => sub { shift; $fd=<shift>; $fd->shutdown; remove($fd)... }
                   -or-
                   sub { my $ctx = shift; $ctx->exit unless $stream->err(shift) }
        );

    1
    $ios->loop(@read_fds, @write_fds, @err_fds);

    2
    my $ctx = $ios->create_context(@read_fds, @write_fds, @err_fds);
    while ($ctx->iterate);

    3
    while ($ctx->iterate) {
        # ...
        if (connect_command) {
            $ios = $ios->merge(new cmt::ios(...));
            $ctx = $ios->create_context;
        }
    }

    4
    $mios = $ios->merge();
    $mctx = $mios->create_context;
    while ($mctx->iterate) {
        if (connect_command) {
            $sock = new socket;
            $mios->merge(new cmt::ios(GROUP=>[$sock], ...));
            $mctx->add($sock);
        }
    }

    5
    new cmt::ios {
        -read = {
            my ($ctx, $sock) = @_;
            if (connect_command) {
                new socket;
                $ctx->add($socket);
            }
        }
    }

=head1 DESCRIPTION

B<cmt::ios> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::ios-RESOLVES.

=head1 SUB-PACKAGE

=cut
{
    package cmt::ios::context;

    sub new {
        my $class   = shift;
        my $ios     = shift;
        my %vars    = @_;
        my $this    = bless \%vars, $class;
        $this->{IOS}= $ios;
        return $this;
    }

    sub add {
        my $this    = shift;
        my $sel;
        $sel = $this->reads;    $sel->add(@_) if defined $sel;
        $sel = $this->writes;   $sel->add(@_) if defined $sel;
        $sel = $this->errs;     $sel->add(@_) if defined $sel;
    }

    sub remove {
        my $this    = shift;
        my $sel;
        $sel = $this->reads;    $sel->remove(@_) if defined $sel;
        $sel = $this->writes;   $sel->remove(@_) if defined $sel;
        $sel = $this->errs;     $sel->remove(@_) if defined $sel;
    }

    sub add_main {
        my $this    = shift;
        $this->add(@_);
        if (defined (my $idx = $this->ios->{FD_IDX})) {
            $idx->{$_} = $this->ios->{MAIN} for @_;
        }
    }

    sub remove_main {
        my $this    = shift;
        $this->remove(@_);
        if (defined (my $idx = $this->ios->{FD_IDX})) {
            delete $idx->{$_} for @_;
        }
    }

    sub count {
        my $this    = shift;
        my $count   = 0;
        my $sel;
        $sel = $this->reads;    $count += $sel->count if defined $sel;
        $sel = $this->writes;   $count += $sel->count if defined $sel;
        $sel = $this->errs;     $count += $sel->count if defined $sel;
        return $count;
    }

    sub exists {
        my $this    = shift;
        my $sel;
        $sel = $this->reads;    return 1 if defined $sel && $sel->exists(@_);
        $sel = $this->writes;   return 1 if defined $sel && $sel->exists(@_);
        $sel = $this->errs;     return 1 if defined $sel && $sel->exists(@_);
        return undef;
    }

    sub iterate     { shift->{ITERATOR}->() }
    sub next        { my $next = shift->{NEXT}; $$next = 1 }
    sub exit        { my $next = shift->{NEXT}; $$next = 0 }

    # cmt::ios
    sub ios         { shift->{IOS} }

    # IO::Select
    sub reads       { shift->{READS}  }
    sub writes      { shift->{WRITES} }
    sub errs        { shift->{ERRS}   }

    # user data
    sub stat        { shift->{STAT}   }
}

{
    package cmt::ios::merged;

    use cmt::util;
    use Data::Dumper;

    our @ISA    = qw(cmt::ios);

    sub new {
        my $class   = shift;

        my %groups;
        my %fd_idx;     # fd -> ios-object

        my $merged  = bless {
            MAIN    => $_[0],
            SUBS    => [],
            GROUPS  => \%groups,
            FD_IDX  => \%fd_idx,
        }, $class;

        $merged->merge(@_);

        $merged->{-init} = sub { *__ANON__ = '<init>';
            # this should return the last retval.
            fire_sub($_, '-init', @_) for $merged->subs;
        };

        for my $method qw(-read -write -err) {
            $merged->{$method} = sub { *__ANON__ = '<'.$method.'>';
                my ($ctx, $fd) = @_;
                my $sub_ios = $merged->fdindex($fd);
                fire_sub($sub_ios, $method, @_);
            }
        }
        return $merged;
    }

    sub merge {
        my $this    = shift;

        my $subs    = $this->{SUBS};
        my $groups  = $this->{GROUPS};
        my $fd_idx  = $this->{FD_IDX};

        push @$subs, @_;

        for my $child (@_) {
            cmt::ios::_log2 "merge child $child\n";
            my $child_groups = $child->{GROUPS};
            for my $gname (keys %$child_groups) {
                my $fd_set = $child_groups->{$gname};

                # building reverse-index
                for (@$fd_set) {
                    if (defined (my $child_0 = $fd_idx->{$_})) {
                        # die "overlapped group $gname: $_ in $child_0 and $child"
                    }
                    $fd_idx->{$_} = $child;
                }

                # merge group
                my $all = $groups->{$gname};
                   $all = $groups->{$gname} = [] unless defined $all;
                push @$all, @$fd_set;
            }
        }
        return $this;
    }

    sub remove {
        my $this = shift;

        my $subs    = $this->{SUBS};
        my $groups  = $this->{GROUPS};
        my $fd_idx  = $this->{FD_IDX};

        for my $child (@_) {
            cmt::ios::_log2 "remove child $child\n";
            array_remove @$subs, $child;

            my $child_groups = $child->{GROUPS};
            for my $gname (keys %$child_groups) {
                my $fd_set = $child_groups->{$gname};
                my $all = $groups->{$gname};
                   $all = $groups->{$gname} = [] unless defined $all;
                for my $fd (@$fd_set) {
                    # remove reverse-index
                    delete $fd_idx->{$fd};
                    # subtract from the group
                    array_remove @$all, $fd;
                }
                # remove empty groups
                delete $groups->{$gname} unless @$all;
            }
        }
    }
}

=head1 METHOD

=cut
sub new {
    my $class   = shift;
    my $this    = bless {}, $class;
    my %config  = @_;
    my %groups  = (
        READ    => [],          # default group for can_read
        WRITE   => [],          # default group for can_write
        ERR     => [],          # default group for error_test
    );
    for (keys %config) {
        my $val = $config{$_};
        if (/^(-\w+)$/) {       # -init, -read, -write, -err, etc.
            $this->{$1} = $val;
        } elsif (/^\w+$/) {     # fd-group, an IO::Select for each group
            $groups{$_} = $val;
        } else {
            # croak ?
            die "Invalid key name $_";
        }
    }
    $this->{GROUPS} = \%groups;
    return $this;
}

sub merge {
    # $_[0] == $this
    new cmt::ios::merged(@_)
}

sub remove {
    die "Can't remove: not a merged ios";
}

sub create_context {
    my $this        = shift;
    my $readg       = shift || 'READ';
    my $writeg      = shift || 'WRITE';
    my $errg        = shift || 'ERR';
    my ($readf, $writef, $errf, $idlef) =
        ($this->{-read}, $this->{-write}, $this->{-err}, $this->{-idle});
    my $mask        = RR | WW | EE;

    my $st_select   = 0;
    my $st_read     = 0;
    my $st_write    = 0;
    my $st_err      = 0;
    my $st_idle     = 0;

    my $st_read_op  = 0;        # fx: take effects.
    my $st_write_op = 0;
    my $st_err_op   = 0;

    my $max_timeout = 1.0;      # seconds
    my $read_t      = undef;    # read timeout
    my $write_t     = undef;    # write timeout
    my $err_t       = undef;    # err timeout

    my %stat = (
        'select'    => \$st_select,
        'read'      => \$st_read,
        'write'     => \$st_write,
        'err'       => \$st_err,
        'idle'      => \$st_idle,
        'read_op'   => \$st_read_op,
        'write_op'  => \$st_write_op,
        'err_op'    => \$st_err_op,
        'read_t'    => \$read_t,
        'write_t'   => \$write_t,
        'err_t'     => \$err_t,
        );

    my $reads       = new IO::Select($this->group($readg))  if defined $readg;
    my $writes      = new IO::Select($this->group($writeg)) if defined $writeg;
    my $errs        = new IO::Select($this->group($errg))   if defined $errg;

    my $next        = 1;

    my $context = new cmt::ios::context(
        $this,
        READS   => $reads,
        WRITES  => $writes,
        ERRS    => $errs,
        STAT    => \%stat,
        NEXT    => \$next,
    );

    $context->{ITERATOR} = sub { *__ANON__ = '<ITERATOR>';
        my $timeout = zero_to_undef(min(
            $read_t, $write_t, $err_t, $max_timeout));
        my $_begin = ftime;                         # BUGFIX

        my @all = IO::Select->select(
            $mask & RR ? $reads  : undef,
            $mask & WW ? $writes : undef,
            $mask & EE ? $errs   : undef,
            $timeout);

        my $_select_cost = ftime - $_begin;         # BUGFIX

        my @fds_read    = $all[0] ? @{$all[0]} : ();
        my @fds_write   = $all[1] ? @{$all[1]} : ();
        my @fds_err     = $all[2] ? @{$all[2]} : ();

        my @resp_read   = map {  $readf->($context, $_) } @fds_read    if defined $readf;
        my @resp_write  = map { $writef->($context, $_) } @fds_write   if defined $writef;
        my @resp_err    = map {   $errf->($context, $_) } @fds_err     if defined $errf;

        $st_select++;
        $st_read        += my $nread    = count_undefs @fds_read;
        $st_write       += my $nwrite   = count_undefs @fds_write;
        $st_err         += my $nerr     = count_undefs @fds_err;
        $st_read_op     += my $read_op  = count_undefs @resp_read;
        $st_write_op    += my $write_op = count_undefs @resp_write;
        $st_err_op      += my $err_op   = count_undefs @resp_err;
        my $nall        = $nread   + $nwrite   + $nerr;
        my $all_op      = $read_op + $write_op + $err_op;

        # select() should wait for TIMEOUT before return empty list.
        fsleep $timeout - (ftime -$_begin)          # BUGFIX
            if $nall == 0 and $_select_cost < $timeout;

        # if the event is ignored, than slow down the loop.
        # reset the mask, so give chance to any operations next time.
        $mask = RR | WW | EE;
        if (@fds_read) {
            if ($read_op == 0) {
                $mask &= ~RR; double $read_t;
            } else {
                $mask |= RR;  undef $read_t;         #inf
            }
        }
        if (@fds_write) {
            if ($write_op == 0) {
                $mask &= ~WW; double $write_t;
            } else {
                $mask |= WW;  undef $write_t;        #inf
            }
        }
        if (@fds_err) {
            if ($err_op == 0) {
                $mask &= ~EE; double $err_t;
            } else {
                $mask |= EE;  undef $err_t;          #inf
            }
        }
        if ($all_op == 0) {
            if (defined $idlef) {
                $idlef->($context);
                $st_idle++;
            }
        }
        return $next;
    };

    my $initf = $this->{-init};
       $initf->($context, $this) if defined $initf;
    return $context;
}

sub loop {
    my $this    = shift;
    my $ctx     = $this->create_context(@_);
    my $iterator= $ctx->{ITERATOR};
    while ($iterator->()) {
    }
    # ctx->DESTROY.
    return $ctx->{STAT};
}

# utilities

sub group {
    my ($this, $name) = @_;
    my $fd_set;
    $fd_set = $this->{GROUPS}->{$name};
    die "Group $name isn't existed" unless ref($fd_set) eq 'ARRAY';
    return @$fd_set;
}

sub subs {
    my $this = shift;
    my $subs = $this->{SUBS};
       $subs = [] unless defined $subs;
    return @$subs;
}

sub fdindex {
    my $this = shift;
    my $fd = shift;
    my $idx = $this->{FD_IDX};
    die "This $this is not a merged cmt::ios" unless defined $idx;
    my $ios = $idx->{$fd};
    #die "Not found $fd in FD_IDX of $ios" unless defined $ios;
    return $ios;
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)
sub count_undefs {
    my $count = 0;
    for (@_) {
        $count++ if defined $_;
    }
    return $count;
}

sub zero_to_undef {
    my $x = shift;
    (defined $x && $x == 0) ? undef : $x;
}

sub double(\$) {
    my $x = shift;
    $$x = $$x ? (2 * $$x) : 1;
}

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"I/O Stream">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1