
package webcon::fext::MP3;

use strict;
use FileHandle;
use webcon::fext::BinLayout;
use webcon::fext::util::ID3v1;
use Exporter;
use vars qw/@ISA @EXPORT/;


my @MP3_HEADER = (
	['version'],
	['lay'],
	['error_protection'],
	['bitrate_index'],
	['sampling_frequency'],
	['padding'],
	['extension'],
	['mode'],
	['mode_ext'],
	['copyright'],
	['original'],
	['emphasis'],
	);

my $MP3_HEADER_SIZE = layout_normalize(\@MP3_HEADER);


sub get_summary {
	my ($filename) = @_;

	# read header
	my $summary = layout_parse_fileheader(\@MP3_HEADER, $filename);
	return undef if (!$summary);  	# read header failure

	# read music info
	my $fh = new FileHandle $filename;
	$fh->seek();
	return $summary;
}

sub ext_info {
	{
		'method_summary'		=> \&get_summary,
	};
}

@ISA = qw(Exporter);
@EXPORT = qw(
	ext_info
	);

1;