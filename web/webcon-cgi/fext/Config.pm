
package webcon::fext::Config;

use strict;
use Exporter;
use webcon::Config;
use webcon::FileBase;
use vars qw/@ISA @EXPORT/;
use vars qw/%MIME %EXT/;

my $DEF_MIME = $WConf{'default_mime'};

%MIME = (
#	ext-name		 MIME-type, 		ext-info
	'htm' 		=> [ 'text/html',		undef, ],
	'html'		=> [ 'text/html', 		undef, ],
	'txt'		=> [ 'text/plain', 		undef, ],
	'gif'		=> [ 'image/gif', 		undef, ],
	'jpg'		=> [ 'image/jpeg', 		undef, ],
	'jpeg'		=> [ 'image/jpeg', 		undef, ],
	'bmp'		=> [ 'image/bitmap', 	undef, ],
	'xbm'		=> [ 'image/x-bitmap', 	undef, ],
	'mp3'		=> [ $DEF_MIME, 		'mp3::ext_info', ],
	'.other'	=> [ $DEF_MIME, ],
);

%EXT = (
	'zip' => {
		'.view' 		=> 'zip::listdir',
		'Unzip' 		=> 'zip::unzip',
		},
	'gz' => {
		'Gunzip' 		=> 'gz::gunzip',
		},
	'tar' => {
		'Extract' 		=> 'tar::extract',
		},
	'c' => {
		'Compile' 		=> 'c::compile',
		},
	'cpp' => {
		'Compile' 		=> 'c::compile',
		},
	'cxx' => {
		'Compile' 		=> 'c::compile',
		},
	'.text' => {
		'.modify'		=> 'text::modify',
		},
);


sub mime_type {
	my ($ext) = @_;
	my $mime = $MIME{$ext} || $MIME{'.other'};
	return $mime->[0];
}

sub mime_ext_info {
	my ($ext) = @_;
	my $mime = $MIME{$ext} || $MIME{'.other'};
	return $mime->[1];
}

sub mime_load_ext {
	my ($extname) = @_;
	my $extinfo = 'webcon::FileType::'.mime_ext_info($extname);
	my ($extpkg) = $extinfo =~ m/((?:[^:]+::)*)/;
	$extpkg =~ s/::/$SLASH_CHR/g;
	require "$extpkg.pm";
	my $ext = eval("&".$extinfo);
	return $ext;
}

sub mime_summaries {
	my ($filename) = @_;
	my $extname = path_getext $filename;
	my $ext = mime_load_ext $extname;
	my $get_summ_proc = $ext->{'method_summary'};
	if ($get_summ_proc) {
		return &$get_summ_proc($filename)
	}
	return undef;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	%MIME %EXT
	mime_type
	mime_ext_info
	mime_load_ext
	mime_summaries
	);

1;
