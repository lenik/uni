package cmt::path;

=head1 NAME

cmt::path - path utilities

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::lang('_def');
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA        = qw(Exporter);
our @EXPORT     = qw(path_normalize
                     path_split
                     path_splitext
                     path_join
                     temp_path
                     mkdir_p
                     which
                     );
our @EXPORT_OK  = qw($SLASH $ANYSLASH
                     path_segs
                     path_comp
                     dir_size
                     _H _S
                     qg
                     );

# INITIALIZORS
our $SLASH      = '/';
our $ANYSLASH   = '/\\\\';

our $opt_win32  = $^O eq 'MSWin32';
    if ($opt_win32) {
        eval "use Win32::File('GetAttributes'); 1"
            or die "failed to load Win32::File: $!";
    }

=head1 SYNOPSIS

    use cmt::path;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::path> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::path-RESOLVES.

=head1 FUNCTIONS

=cut

=head2 Path functions based on literal

=cut
=head3      normalize path

=cut
sub path_normalize($) {
    my $path = shift;
    # print "path_normalize $path" if $opt_verbose;

    return '.' if !$path;

    # remove trailing ..../..(//)
    $path =~ s/[$ANYSLASH]*$//;

    # match pure-root (///../)
    return $SLASH if !$path;

    # root-absolute flag
    my $root = $path =~ s/^[$ANYSLASH]+// ? $SLASH : '';

    my @src = split(/[$ANYSLASH]/, $path);
    my @dest;
    foreach (@src) {
        if ($_ eq '..') {
            if ($dest[-1] eq '..') {
                push @dest, '..';
            } elsif (!@dest) {
                push @dest, '..' unless $root;
            } else {
                pop @dest;
            }
        } elsif ($_ eq '' or $_ eq '.') {
            # skip
        } else {
            push @dest, $_;
        }
    }
    $path = $root.join($SLASH, @dest) || '.';
    # print " -> $path\n" if $opt_verbose;
}

=head3      split dirname and filename

=cut
sub path_split($) {
        no warnings('uninitialized');
    my $path = shift;
    my ($dir, $name) =
            ($path =~ m/^(?:(.*)[$ANYSLASH])?([^$ANYSLASH]*)$/);
    _def($dir, '');
    wantarray ? ($dir, $name) : $name;
}

=head3      split filename and extname

=cut
sub path_splitext($) {
    my $path = shift;
    my ($dir, $name) = path_split($path);
    my ($base, $ext) =
            ($name =~ m/^(.*?)((?:\.[^.]*)?)$/);
    wantarray ? ($base, $ext) : $ext;
}

=head2 Path functions based on segments

=cut
=head3      path_segs: split into segments

=cut
sub path_segs($) {
    my $path = shift;
    return undef unless defined $path;
    split(/[$ANYSLASH]/, $path);
}

=head3      path_comp: compact path

=cut
sub path_comp {
    return '.' if !scalar(@_);

    # print "path_comp [".join(',', @_)."]" if $opt_verbose;

    my $root = $_[0] ? '' : $SLASH;
    my @dest;
    foreach (@_) {
        if ($_ eq '..') {
            if (! @dest) {
                push @dest, '..';
            } elsif ($dest[-1] eq '..') {
                push @dest, '..';
            } else {
                pop @dest;
            }
        } elsif ($_ eq '' or ($_ eq '.')) {
            # skip
        } else {
            push @dest, $_;
        }
    }
    my $path = $root.join($SLASH, @dest);
    # print " -> $path\n" if $opt_verbose;
}

=head3      path_join: join paths

=cut
sub path_join {
    my @seqs;
    my $test_root;
    $test_root = qr/^[$ANYSLASH]/;
    # if ("os" == "windows") {
    $test_root = qr/^([$ANYSLASH]|[A-Za-z]:)/;

    foreach (@_) {
        my @this_segs = path_segs $_;
        if (defined $_ and /$test_root/) {
            @seqs = @this_segs;
        } else {
            push @seqs, @this_segs;
        }
    }
    path_comp(@seqs);
}

=head2 Filesystem utilities

=cut
=head3      dir_size

=cut
sub dir_size {
    my ($path, $rec) = @_;
    # TODO - ...
}

my $_TEMP_HOME;
=head3      temp_home

=cut
sub temp_home {
    unless (defined $_TEMP_HOME) {
        my $t = $ENV{'TEMP'};
        $t = $ENV{'TMP'} if (!$t);
        if (!$t) {
            mkdir '/tmp' if (! -e '/tmp');
            $t = '/tmp';
        }
        $t =~ s/[\/\\]$//;
        $_TEMP_HOME = $t;
    }
    return $_TEMP_HOME;
}

=head3      temp_path

=cut
our $_uid = 0;
sub temp_path {
    my $name = shift;
    my $uid = $$.'_'.$_uid++;
    if (defined $name) {
        $name =~ s/\?/$uid/g;
    } else {
        $name = "cmt_$uid.tmp";
    }
    return temp_home . $SLASH . $name;
}

=head3      qg: quote globbing pattern

=cut
sub qg {
    my $p = shift;
    $p =~ s/([.+@\#\$^&()])/\\$1/g;
    $p =~ s/\*/.*/g;
    $p =~ s/\?/./g;
    return qr/^$p$/i;
}

=head3      mkdir_p

=cut
sub mkdir_p {
    my $path = shift;
    return 1 if -d $path;
    return 1 if mkdir $path;
    my $i = rindex($path, '/');
    my $j = rindex($path, '\\');
    my $last = $i > $j ? $i : $j;
    return 0 if $last < 0;
    return 0 unless mkdir_p(substr($path, 0, $last));
    return 1 if -d $path;
    mkdir $path;
}

=head3      _H

=cut
sub _H {
    my $path = shift;
    return undef unless -e $path;
    if ($opt_win32) {
        GetAttributes($path, my $attrib);
        return 1 if $attrib & 2;
    }
    my (undef, $base) = path_split $path;
    return 1 if $base =~ /^\./;
    return 0;
}

my %_SYSFILES = (
    '.svn'  => 1,
    'CVS'   => 1,
);
=head3      _S

=cut
sub _S {
    my (undef, $base) = path_split shift;
    return 1 if $_SYSFILES{$base};
    return 0;
}

=head3      which

=cut
sub which {
    my $name    = shift;
    my @PATH    = split(';', $ENV{PATH});       # unix ':'
    my @PATHEXT = split(';', $ENV{PATHEXT});    # unix ':'
    for my $path (@PATH) {
        my $pn = path_join($path, $name);
        return $pn if -f $pn;
        for my $ext (@PATHEXT) {
            my $pnx = $pn.$ext;
            return $pnx if -f $pnx;
        }
    }
    return undef;
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