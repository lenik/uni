
package webcon::FileBase;

use strict;
use Digest::MD5 qw(md5_hex);
use Exporter;
#use os-portable
use vars qw/@ISA @EXPORT/;
use vars qw/$SLASH_CHR $SLASH_SET $SLASH_PAT/;


$SLASH_CHR = "/";
$SLASH_SET = "\\/";
$SLASH_PAT = "[$SLASH_SET]";

sub make_id {
	my ($text) = @_;
	md5_hex($text);
}

sub restore_text {
	my ($id, $list) = @_;
	foreach (@$list) {
		my $h = md5_hex($_);
		if ($id eq $h) {
			return $_;
		}
	}
	return undef;
}


sub path_normalize {
	my ($path) = @_;

	return '.' if !$path;
	return $path if $path =~ m/^$SLASH_PAT$/;

	# remove trailing /
	while ($path =~ s/$SLASH_PAT$//) {}

	my $root = $path =~ s/^$SLASH_PAT+// ? $SLASH_CHR : '';

	my @src = split($SLASH_PAT, $path);
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
		} elsif (!$_ or ($_ eq '.')) {
			# skip
		} else {
			push @dest, $_;
		}
	}

	$path = $root.join($SLASH_CHR, @dest) || '.';
}

sub path_split {
	my ($path) = @_;
	my ($dir, $name) =
		($path =~ m/^(.*$SLASH_PAT)?([^$SLASH_SET]*)$/);
	wantarray ? ($dir, $name) : $name;
}

sub path_segs {
	my ($path) = @_;
	split($SLASH_PAT, $path);
}

sub path_comp {
	return '.' if !scalar(@_);

	my $root = $_[0] ? '' : $SLASH_CHR;
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
	return $root.join($SLASH_CHR, @dest);
}

sub path_join {
	my @seqs;
	my $test_root = qr/^$SLASH_PAT/;
	# if ("os" == "windows") {
		$test_root = qr/^($SLASH_PAT|[A-Z]:)/;

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

sub path_getext {
	my ($path) = @_;
	my ($dir, $name) = path_split($path);
	my ($base, $ext) =
		($name =~ m/^(.*?)(\.[^.]*)?$/);
	wantarray ? ($base, $ext) : $ext;
}


@ISA = qw(Exporter);
@EXPORT = qw(
	$SLASH_CHR $SLASH_SET $SLASH_PAT
	make_id restore_text
	path_normalize path_join path_split path_getext path_parts
	);

1;
