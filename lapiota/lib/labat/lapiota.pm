package labat::lapiota;

=head1 NAME

labat::lapiota - Lapiota System Functions

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(3);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = $labat::LOGLEVEL - 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Cwd('abs_path');
use labat;
use Exporter;
use Getopt::Long;

our @ISA    = qw(Exporter);
our @EXPORT = qw(getopts
                 findabc
                 findexist
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use lapiota;
    mysub(arguments...)

=head1 Lapiota System Functions

B<lapiota> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-lapiota-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub getopts(\@@) {
    my @bak = @ARGV;
    my $a = shift;
    @ARGV = @$a;
    my $result = GetOptions(@_);
    @$a = @ARGV;
    @ARGV = @bak;
    $result
}

sub findabc {
    my $ctx = shift if ref $_[0];
    my $show = 0;
    my $last = 0;
    my $style = 'm';
    my $escape = 0;
    getopts(@_,
        'last|z'    => \$last,
        'show|p'    => \$show,
        'unix|u'    => sub { $style = 'u' },
        'windows|w' => sub { $style = 'w' },
        'mixed|m'   => sub { $style = 'm' },
        'escape|e'  => \$escape,                # using \\, \/ instead of \, /
        );

    _log2 'context: $ctx' if defined $ctx;
    _log2 'option: show' if $show;
    _log2 'option: last' if $last;
    _log2 'option: list' if wantarray;
    _log2 'option: escape' if $escape;

    die "no package name specified" unless @_;
    my $package = shift;
    my $chdir = $package =~ s/\/$//;

    my @root = @_;
    unless (@root) {
        my $lam_root = $ENV{'LAM_ROOT'} || 'c:/lam';
            $lam_root =~ s/\\/\//g;  # can't glob with `\', like C:\*
        _log2 "start from default lam-root: $lam_root";
        @root = <$lam_root/*>;
        _log3 "default roots: @root";
    }

    my $_home;
    my @home;
 R: while (@root) {
        my $root = shift @root;
        _log2 "root: $root";
        $root =~ s{^/(cygdrive|mnt)/([a-z])/}{$2:/} unless $style eq 'u';

        unshift @root, grep {-d} (<$root/*.d>, <$root/\\\[*\\\]>);

        my $_xdir = '';
     X: while (wantarray or !defined $_home) {
            _log2 "  xdir: $_xdir";
            my $prefix = $package;
            for my $f (<$root/$_xdir$prefix*>) {
                next if ! -d $f;
                $_home = $f if (!defined $_home) or $last;
                if (wantarray) {
                    push @home, $_home;
                } elsif (!$last) {
                    last;
                }
            }
            if (defined $_home) {
                if (wantarray) {
                    last;
                } else {
                    last R;
                }
            }

            # do { if exist p/prefix -> xdir-loop } while chop(prefix)
            while (length($prefix) > 0) {
                if (-d "$root/$_xdir$prefix") {
                    $_xdir .= $prefix . '/';
                    next X;
                }
                chop $prefix;
            }
            last;
        }
    }

    return undef unless defined $_home;

    my $DFS = $style eq 'w' ? '\\' : '/';
       $DFS = '\\'.$DFS if $escape;
    $_home =~ s/\//$DFS/g;
    @home = map { s/\//$DFS/g; $_ } @home;

    chdir $_home if $chdir;

    $ENV{'_HOME'} = $_home;
    if ($show) {
        if (wantarray) {
            print "$_\n" for @home;
        } else {
            print "$_home\n";
        }
    }

    return wantarray ? @home : $_home;
}

sub findexist {
    my $ctx = shift;
    my $f   = sub { -e shift };
    getopts(@_,
        'directory' => sub { $f = sub { -d shift } },
        'file'      => sub { $f = sub { -f shift } },
        );
    my $last = '<empty-arg-list>';
    my @args = map { labat::_resolv($ctx, $_) } @_;
    for (@args) {
        last if $f->($last = $_);
    }
    return $last;
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