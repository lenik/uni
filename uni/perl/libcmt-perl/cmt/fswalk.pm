package cmt::fswalk;

=head1 NAME

cmt::fswalk - files iterator

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::lang('_or');
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::path('path_join', 'path_split', '_H', '_S');
use cmt::util('get_named_args', 'listdir');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Cwd('cwd');
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(fswalk
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use cmt::fswalk;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::fswalk> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::fswalk-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 fswalk(ITERATOR-CALLBACK, OPTIONS)

=head3 OPTIONS
    -start=START-PATH
    -filter=REGEXP or CALLBACK
        only files matched the specified filter is iterated.
    -hidden=1
        include hidden files
    -depth=N
        how depth recurse into the sub-directories,
        only START-PATH is iterated if -depth=0
    -order=[bd]
        'b' breadth-first
        'd' depth-first
    -leave=1
        call ITERATOR->(DIR, 1) when scanner leave out the directory
    -sort=COMPARATOR
        how to sort files
    -inclusive=0
        exclude the START path itself, only iterater descendants

=cut
sub fswalk(&;@) {
        no warnings('uninitialized');
    my $cb      = shift;    # (file, [leave])
    my %cfg     = get_named_args @_;
    my $start   = _or($cfg{-start}, '.');
    my $filter  = $cfg{-filter};
    if (defined $filter) {
        my $fdirs = $cfg{-filterdirs};
        my $filterf = ref $filter eq 'Regexp' ? sub { /$filter/ } : $filter;
        my $_cb = $cb;
        $cb = sub { my $fp = -f $_[0];
                    my $ok = ($fp || $fdirs) && $filterf->(@_);
                    $ok ? $_cb->(@_) : ($fp ? 0 : 1) };
    }
    my $hidden  = $cfg{-hidden};
    my $depth   = _or($cfg{-depth}, 999);
    my $bfirst  = index('bw', $cfg{-order}); # breadth-first
    my $leave   = $cfg{-leave};
    my $sort    = $cfg{-sort};
    my $excl    = not $cfg{-inclusive};     # include the start file
    my $iter;   # (start, level)
       $iter    = sub { *__ANON__ = '<iter>';
        my $start = shift;
        my $dir   = $start;
        my $level = shift;
        my @files;
        if (-d $start) {
            $dir = $start;
            # start-dir, [incl], excl, filter, sort
            @files = listdir($dir, undef, qr/^\.\.?$/,
                             $hidden ? undef : sub { my $p = path_join @_;
                                                     ! (_H($p) || _S($p)) },
                             $sort);
        } else {
            my $fpat;
            ($dir, $fpat) = path_split($start);
            $dir = '.' if $dir eq '';
            my $cwd = cwd();
            chdir($dir) or die "can't chdir to $dir: $!";
            @files = grep { -e "$dir/$_" } glob $fpat;
            chdir($cwd) or die "can't chdir to $cwd: $!";
        }
        my $ret;
        my $count = 0;
        my @dirs;
        for (@files) {
            my $path = path_join($dir, $_);
            if (-d $path) {
                next if $level >= $depth;
                if ($bfirst) {
                    push @dirs, $path;
                } else {
                    # BREADTH-FIRST-COPY-BEGIN
                    $ret = $cb->($path);
                    return -1 if $ret == -1;    # break
                    next      if $ret == 0;     # ignore
                    $count += $ret - 1;
                    $ret = $iter->($path, $level + 1);
                    return -1 if $ret == -1;    # break
                    $count += $ret;
                    $cb->($dir, 1) if $leave;
                    # BREADTH-FIRST-COPY-END
                }
            } else {
                $ret = $cb->($path);
                return -1 if $ret == -1;        # break
                $count += $ret;
            }
        }
        for (@dirs) {
            # BREADTH-FIRST-COPY-BEGIN
            $ret = $cb->($_);
            return -1 if $ret == -1;    # break
            next      if $ret == 0;     # ignore
            $count += $ret - 1;
            $ret = $iter->($_, $level + 1);
            return -1 if $ret == -1;    # break
            $count += $ret;
            $cb->($dir, 1) if $leave;
            # BREADTH-FIRST-COPY-END
        }
        $count
    };
    if ($excl) {
        $iter->($start, 0)
    } else { # include the start file
        my $count = $cb->($start);
        return $count if $count <= 0;       # break or ignore
        my $ret = $iter->($start, 0);
        return -1 if $ret == -1;            # break
        $cb->($start, 1) if $leave;
        $count = $count - 1 + $ret
    }
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

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
