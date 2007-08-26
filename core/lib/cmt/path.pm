package cmt::path;

use strict;
# use Digest::MD5 qw(md5_hex);
use Exporter;

our @ISA        = qw(Exporter);
our @EXPORT     = qw($charFS
                     $setFS
                     $patFS
                     path_normalize
                     path_split
                     path_splitext
                     path_segs
                     path_comp
                     path_join
                     dir_size
                     temp_path
                     mkdir_p
                     ishidden
                     which
                     );
our @EXPORT_OK  = qw(qg);

our $opt_win32  = $^O eq 'MSWin32';

    if ($opt_win32) {
        eval "use Win32::File('GetAttributes'); 1"
            or die "failed to load Win32::File: $!";
    }

our $charFS     = "/";
our $setFS      = "\\\\/";
our $patFS      = "[$setFS]";

my $opt_verbose = 1;

# path functions based on literal

    # normalize path
    sub path_normalize($) {
        my $path = shift;
        # print "path_normalize $path" if $opt_verbose;

        return '.' if !$path;

        # remove trailing ..../..(//)
        $path =~ s/$patFS*$//;

        # match pure-root (///../)
        return $charFS if !$path;

        # root-absolute flag
        my $root = $path =~ s/^$patFS+// ? $charFS : '';

        my @src = split($patFS, $path);
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
        $path = $root.join($charFS, @dest) || '.';
        # print " -> $path\n" if $opt_verbose;
    }

    # split dirname and filename
    sub path_split($) {
        my $path = shift;
        my ($dir, $name) =
                ($path =~ m/^(.*$patFS)?([^$setFS]*)$/);
        wantarray ? ($dir, $name) : $name;
    }

    # split filename and extname
    sub path_splitext($) {
        my $path = shift;
        my ($dir, $name) = path_split($path);
        my ($base, $ext) =
                ($name =~ m/^(.*?)(\.[^.]*)?$/);
        wantarray ? ($base, $ext) : $ext;
    }

# path functions based on segments

    # split into segments
    sub path_segs($) {
        my $path = shift;
        split($patFS, $path);
    }

    # compact path
    sub path_comp {
        return '.' if !scalar(@_);

        # print "path_comp [".join(',', @_)."]" if $opt_verbose;

        my $root = $_[0] ? '' : $charFS;
        my @dest;
        foreach (@_) {
            if ($_ eq '..') {
                if ($dest[-1] eq '..') {
                    push @dest, '..';
                } elsif (!@dest) {
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
        my $path = $root.join($charFS, @dest);
        # print " -> $path\n" if $opt_verbose;
    }

    # join paths
    sub path_join {
        my @seqs;
        my $test_root;
        $test_root = qr/^$patFS/;
        # if ("os" == "windows") {
        $test_root = qr/^($patFS|[A-Za-z]:)/;

        foreach (@_) {
            my @this_segs = path_segs $_;
            if ($_ =~ m/$test_root/) {
                @seqs = @this_segs;
            } else {
                push @seqs, @this_segs;
            }
        }
        path_comp(@seqs);
    }

# filesystem utilities

    sub dir_size {
        my ($path, $rec) = @_;
        # TODO - ...
    }

    my $_TEMP_HOME;
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

    sub temp_path {
        my $name = shift;
        if (defined $name) {
            $name =~ s/\?/$$/g;
        } else {
            $name = "cmt_$$.tmp";
        }
        return temp_home . $charFS . $name;
    }

    # quote globbing pattern
    sub qg {
        my $p = shift;
        $p =~ s/([.+@\#\$^&()])/\\$1/g;
        $p =~ s/\*/.*/g;
        $p =~ s/\?/./g;
        return qr/^$p$/i;
    }

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

    sub ishidden {
        my $path = shift;
        return undef unless -e $path;
        if ($opt_win32) {
            GetAttributes($path, my $attrib);
            return 1 if $attrib & 2;
        }
        my ($dir, $base) = path_split $path;
        return 1 if $base =~ /^\./;
        return 0;
    }

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

1
