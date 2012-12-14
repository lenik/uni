
package webcon::fext::BinLayout;

use strict;
use FileHandle;
use Exporter;
use vars qw/@ISA @EXPORT/;


# @layout = (
#	[var-name, 	length-bytes, 	offset-bytes, 	var-description ]
#	[var-name, 	length-bytes, 	offset-bytes, 	var-description ]
#	...
# default:
#				DWORD(4)		CONTINUE(undef)	as: var-name


sub T_BYTE		{ 1 }
sub T_WORD		{ 2 }
sub T_DWORD		{ 4 }
sub T_QWORD		{ 8 }


# normalize layout array
# return the offset for next entry
sub layout_normalize {
	my ($layout) = @_;
	my $units = scalar(@$layout);
	my $i;
	my $last_offset = 0;
	for ($i = 0; $i < $units; $i++) {
		my $unit = $layout->[$i];
		$unit->[1] = &T_DWORD if (!$unit->[1]);
		if (!defined $unit->[2]) {
			$unit->[2] = $last_offset;
		} else {
			$last_offset = $unit->[2];
		}
		$last_offset += $unit->[1];
		$unit->[3] = $unit->[0] if (!$unit->[3]);
	}
	return $last_offset;
}

sub layout_parse {
	my ($layout, $buffer) = @_;
	my %result;
	foreach (@$layout) {
		my ($name, $bytes, $offset, $desc) = @$_;
		my $unit = substr($buffer, $offset, $bytes);
		$result{$name} = $unit;
	}
	return \%result;
}

sub layout_parse_fileheader {
	my ($layout, $path) = @_;
	my %result;
	my $size = layout_normalize $layout;
	my $fh = new FileHandle $path;
	if (!$fh) { return undef; }
	my $buffer;
	if ($size == $fh->read($buffer, $size)) {
		return layout_parse($layout, $buffer);
	}
	return undef;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	T_BYTE T_WORD T_DWORD T_QWORD
	layout_normalize
	layout_parse
	layout_parse_fileheader
	);

1;
