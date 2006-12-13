
package cmt::path;

use strict;
use Digest::MD5 qw(md5_hex);
use Exporter;
#use os-portable
use vars qw/@ISA @EXPORT/;
use vars qw/$charFS $setFS $patFS/;


$charFS = "/";
$setFS  = "\\\\/";
$patFS  = "[$setFS]";


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
				if (!$root) {
					push @dest, '..';
				}
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

	return $path;
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
		} elsif (!$_ or ($_ eq '.')) {
			# skip
		} else {
			push @dest, $_;
		}
	}

	my $path = $root.join($charFS, @dest);
	# print " -> $path\n" if $opt_verbose;

	return $path;
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

@ISA = qw(Exporter);
@EXPORT = qw(
	$charFS
	$setFS
	$patFS

	path_normalize
	path_split
	path_splitext

	path_segs
	path_comp
	path_join

	dir_size
	);

1;
