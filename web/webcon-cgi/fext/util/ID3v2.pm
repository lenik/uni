
package webcon::fext::util::ID3v2;

use strict;
use webcon::fext::util::ID3v1;
use Exporter;
use vars qw/@ISA @EXPORT/;


my @HEADER = (
	['magic', 	3], 		# ='ID3'
	['version', 2], 		# =03H 00H, ID3v2.3.0
	['flags', 	1], 		# =bit(unsync, ext-hdr, experimental, 0 0 0 0 0 0)
	['size', 	4], 		# 28-bits, MSB of each byte isn't used. max=256M
	);

my @FRAME_HEADER = (
	['FCC', 	4], 		# =FourCC
	['size', 	4], 		# 32-bits size of the frame exclude header.
	['flags', 	2], 		# byte 1: (preserve-tag, preserve-file, read-only, 0 0 0 0 0 0)
							# byte 2: (compressed, encrypted, grouping-identity, 0 0 0 0 0 0)
	);

sub get_slash_list;
sub get_ctlist;
sub ms_to_hms;

my %TAGDEF = (
	'TALB' => ['s', 'Title', undef, '(no-title)'],
	'TBPM' => ['n', 'Beats per minute'],
	'TCOM' => ['A', 'Composer', \&get_slash_list],
	'TCON' => ['A', 'Content type', \&get_ctlist],
	'TCOP' => ['s', 'Copyright'],
	'TDAT' => ['s', 'Record-date'],
	'TDLY' => ['n', 'Playlist delay(ms)'],
	'TENC' => ['s', 'Encoded by'],
	'TEXT' => ['A', 'Lyricist/writer', \&get_slash_list],
	'TFLT' => ['s', 'File type', \&get_filetype, 'MPG'],
	'TIME' => ['s', 'Record-time'],
	'TIT1' => ['s', 'Content group description'],
	'TIT2' => ['s', 'Title/Songname/Content description'],
	'TIT3' => ['s', 'Subtitle/Description refinement'],
	'TKEY' => ['s', 'Initial key'], 						# A B C D E F G b #
	'TLAN' => ['s', 'Language'], 							# ISO-639-2
	'TLEN' => ['s', 'Length', \&ms_to_hms], 				# duration length in msec.
	'TMED' => ['A', 'Media type', \&get_mediatype],
	'TOAL' => ['s', 'Original title'],
	'TOFN' => ['s', 'Original filename'],
	'TOLV' => ['A', 'Original lyricist/writer', \&get_slash_list],
	'TOPE' => ['A', 'Original artist/performer', \&get_slash_list],
	'TORY' => ['n', 'Original release year'], 				# as TYER
	'TOWN' => ['s', 'File owner/licensee'],
	'TPE1' => ['A', 'Lead artist/performer', \&get_slash_list],
	'TPE2' => ['s', 'Band/Orchestra/Accompaniment'],
	'TPE3' => ['s', 'Conductor'],
	'TPE4' => ['s', 'Interpreted/remixed/modifyed by'],
	'TPOS' => ['s', 'Part of a set'], 						# "part/total"
	'TPUB' => ['s', 'Publisher'],
	'TRCK' => ['s', 'Track number'], 						# "track/total"
	'TRDA' => ['s', 'Recording dates'],
	'TRSN' => ['s', 'Internet radio station name'],
	'TRSO' => ['s', 'Internet radio station owner'],
	'TSIZ' => ['n', 'Size'], 								# size of audiofile, excluding ID3v2
	'TSRC' => ['s', 'ISRC'], 								# International Standard Recording Code
	'TSSE' => ['s', 'Software/Hardware encoding settings'],
	'TYER' => ['n', 'Year'],
	);


# tag utilities
	sub get_slash_list{
		my ($v) = @_;
		$v ||= $_;
		split("/", $v);
	};

	my %CT_MORE = (
		'RX' => 'Remix',
		'CV' => 'Cover',
		);
	sub get_ctlist {
		my ($v) = @_;
		my $magic = qr/&left-brace;/;
		my (@ct, $item);
		$v ||= $_;
		$v =~ s/\(\(/$magic/g;
		while ($v =~ s/\(([^)]+)\)([^(]*)(?=\(|$)//) {
			if ($1 =~ m/[0-9]+/) { $item = ID3v1_Genre($item); }
			elsif ($CT_MORE{$1}) { $item = $CT_MORE{$item}; }
			else { $item = ''; }
			if ($2 ne $1) {
				$item .= ' ' if length($item) > 0;
				$item .= "$2" if ($2 ne $1);
			}
			$item =~ s/$magic/\(/g;
			push @ct, $item;
		}
		if ($v) { push @ct, $v; }
		return @ct;
	}

	my %FT_MORE = (
		'MPG' => 'MPEG Audio', 			# default
		'/1' => 'MPEG 1/2 layer I',
		'/2' => 'MPEG 1/2 layer II',
		'/3' => 'MPEG 1/2 layer III',
		'/2.5' => 'MPEG 2.5',
		'/AAC' => 'Advanced audio compression',
		'VQF' => 'Transform-domain weighted interleave vector quantization',
		'PCM' => 'Pulse code modulated audio',
		);
	sub get_filetype {
		my ($v) = @_;
		$v ||= $_;
		$FT_MORE{$v} ? $FT_MORE{$v} : $v;
	}

	sub ms_to_hms {
		my ($v) = @_;
		$v = 0+ ($v || $_);
		my ($hour, $min, $sec, $ms);
		$hour = $v / 3600000;
		$v -= 3600000 * $hour;
		$min = $v / 60000;
		$v -= 60000 * $min;
		$sec = $v / 1000;
		$v -= 1000 * $sec;
		$ms = $v;
		return "$hour:$min:$sec.$ms";
	}

	my %MED_MORE = (
		'DIG' 		=> 'Other digital media',
		'DIG/A'		=> 'Analog transfer from media',
		'ANA'		=> 'Other analog media',
		'ANA/WAC'	=> 'Wax cylinder',
		'ANA/8CA'	=> '8-track tape cassette',
		'CD'		=> 'Compact-disc',
		'CD/A'		=> 'Analog transfer from media',
		'CD/DD'		=> 'DDD',
		'CD/AD'		=> 'ADD',
		'CD/AA'		=> 'AAD',
		'LD'		=> 'Laser-disc',
		'LD/A'		=> 'Analog transfer from media',
		'TT'		=> 'Turntable records',
		'TT/33'		=> '33.33 rpm',
		'TT/45'		=> '45 rpm',
		'TT/71'		=> '71.29 rpm',
		'TT/76'		=> '76.59 rpm',
		'TT/78'		=> '78.26 rpm',
		'TT/80'		=> '80 rpm',
		'MD'		=> 'Mini-disc',
		'MD/A'		=> 'Analog transfer from media',
		'DAT'		=> 'DAT',
		'DAT/A'		=> 'Analog transfer from media',
		'DAT/1'		=> 'standard 48 kHz/16 bits, linear',
		'DAT/2'		=> 'mode 2, 32 kHz/16 bits, linear',
		'DAT/3'		=> 'mode 3, 32 kHz/12 bits, nonlinear, low speed',
		'DAT/4'		=> 'mode 4, 32 kHz/12 bits, 4 channels',
		'DAT/5'		=> 'mode 5, 44.1 kHz/16 bits, linear',
		'DAT/6'		=> 'mode 6, 44.1 kHz/16 bits, wide-track play',
		'DCC'		=> 'DCC',
		'DCC/A'		=> 'Analog transfer from media',
		'DVD'		=> 'DVD',
		'DVD/A'		=> 'Analog transfer from media',
		'TV'		=> 'Television',
		'TV/PAL'	=> 'PAL',
		'TV/NTSC'	=> 'NTSC',
		'TV/SECAM'	=> 'SECAM',
		'VID'		=> 'Video',
		'VID/VHS'	=> 'VHS',
		'VID/SVHS'	=> 'S-VHS',
		'VID/BETA'	=> 'BETAMAX',
		'RAD'		=> 'Radio',
		'RAD/FM'	=> 'FM',
		'RAD/AM'	=> 'AM',
		'RAD/LW'	=> 'LW',
		'RAD/MW'	=> 'MW',
		'TEL'		=> 'Telephone',
		'TEL/I'		=> 'ISDN',
		'MC'		=> 'MC(normal cassette)',
		'MC/4'		=> '4.75 cm/s (normal)',
		'MC/9'		=> '9.5 cm/s',
		'MC/I'		=> 'Type I cassette (ferric/normal)',
		'MC/II'		=> 'Type II cassette (chrome)',
		'MC/III'	=> 'Type III cassette (ferric chrome)',
		'MC/IV'		=> 'Type IV cassette (metal)',
		'REE'		=> 'Reel',
		'REE/9'		=> '9.5 cm/s',
		'REE/19'	=> '19 cm/s',
		'REE/38'	=> '38 cm/s',
		'REE/76'	=> '76 cm/s',
		'REE/I'		=> 'Type I cassette (ferric/normal)',
		'REE/II'	=> 'Type II cassette (chrome)',
		'REE/III'	=> 'Type III cassette (ferric chrome)',
		'REE/IV'	=> 'Type IV cassette (metal)',
		);
	sub get_mediatype {
		my ($v) = @_;
		my $magic = qr/&left-brace;/;
		my (@ct, $item);
		$v ||= $_;
		$v =~ s/\(\(/$magic/g;

		# references
		while ($v =~ s/\(([^)]+)\)([^(]*)(?=\(|$)//) {
			my ($refer, $text) = ($1, $2);
			my ($type) = $refer =~ m:^([^/]*)(?=/|$):;
			my $suffix = ($2 ne $1) ? " $2" : '';
			if ($refer =~ m:/[^/]+:) {
				# have parameters
				while ($refer =~ m:(/[^/]+)(?=/|$):g) {
					$item = $MED_MORE{"$type$1"}.$suffix;
					$item =~ s/$magic/\(/g;
					push @ct, $item;
				}
			} else {
				# no parameters
				$item = $MED_MORE{$type}.$suffix;
				$item =~ s/$magic/\(/g;
				push @ct, $item;
			}
		}

		# remains text
		if ($v) { push @ct, $v; }
		return @ct;
	}


# main entries
sub ext_info {
	1;
}


@ISA = qw(Exporter);
@EXPORT = qw(
		ext_info
	);

1;
