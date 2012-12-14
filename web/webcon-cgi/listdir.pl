#!/usr/bin/perl -I../

package webcon;

use strict;
use DirHandle;
use File::stat;
use CGI qw/:standard *table/;
use POSIX qw/strftime/;
use webcon::Config;
use webcon::FileBase;
use webcon::supp::Fcntl qw/:mode/;
use webcon::fext::Config;
if (&isDebug) {
	use Data::Dumper;
}


# listdir
#	string	path
#	int		opt
#

my %BUTTONS = (
	open 		=> 1, 				# IN-PLACE
	modify		=> 2, 				# IN-PLACE, text-only
	remove		=> 4, 				# BOTTOM
	move		=> 8, 				# BOTTOM
	chmod		=> 16, 				# BOTTOM
	mkdir		=> 32, 				# TOP
	mktext		=> 64, 				# TOP
	upload		=> 128, 			# TOP
	summary		=> 256, 			# IN-PLACE
	showhid		=> 4096, 			# <show hiddens>
	recursive	=> 8192, 			# <select recursive>
	);

my $opt = $BUTTONS{open};
my ($path, $path_pre, $path_up);

my $ek_dir = new DirHandle "mat";
my @mat_files = $ek_dir->read;

my (@fnames, @files);
my (@ext_fields);


sub list_getopt {
	$path = param('path')				if defined param('path');
	$opt = param('opt')					if defined param('opt');
	$opt |= $BUTTONS{modify}			if param('modify');
	$opt |= $BUTTONS{remove}			if param('remove');
	$opt |= $BUTTONS{move}				if param('move');
	$opt |= $BUTTONS{chmod}				if param('chmod');
	$opt |= $BUTTONS{mkdir}				if param('mkdir');
	$opt |= $BUTTONS{mktext}			if param('mktext');
	$opt |= $BUTTONS{upload}			if param('upload');
	$opt |= $BUTTONS{summary}			if param('summary');
	$opt |= $BUTTONS{showhid}			if param('showhid');
	$opt |= $BUTTONS{recursive}			if (param('recursive') eq 'on');
}

sub debug_opt {
	my @info;
	push @info, "path: $path";
	push(@info, 'show-hidden') if ($opt & $BUTTONS{showhid});
	foreach (keys %BUTTONS) {
		push(@info, $_) if ($opt & $BUTTONS{$_});
	}
	return @info;
}

sub mode_string {
	my ($mode) = @_;
	# ldbcp- rwxrwxrwx
	#        --s--s--s

	my $s = S_ISDIR($mode) ? 'd' :
			S_ISCHR($mode) ? 'c' :
			#S_ISBLK($mode) ? 'b' :
			#S_ISLNK($mode) ? 'l' :
			#S_ISFIFO($mode)? 'p' :
			#S_ISSOCK($mode)? 's' :
			'-'
			;
	$s = $s
		. (($mode & &S_IRUSR) ? 'r' : '-')
		. (($mode & &S_IWUSR) ? 'w' : '-')
		. (($mode & &S_IXUSR) ? (($mode & &S_ISUID) ? 's' : 'x') : '-')
		. (($mode & &S_IRGRP) ? 'r' : '-')
		. (($mode & &S_IWGRP) ? 'w' : '-')
		. (($mode & &S_IXGRP) ? (($mode & &S_ISGID) ? 's' : 'x') : '-')
		. (($mode & &S_IROTH) ? 'r' : '-')
		. (($mode & &S_IWOTH) ? 'w' : '-')
		. (($mode & &S_IXOTH) ? 'x' : '-')
		;
}

sub time_string {
	my ($t) = @_;
	my @lt = localtime $t;
	strftime("%a %b %e %H:%M:%S %Y", @lt);
}

sub get_file_info {
	my ($name) = @_;
		$name ||= $_;
	my ($ext) = ($name =~ m/\.([^.]*)$/);
	my $full = $path_pre.$name;
	my $st = stat $full;
	my %info;
	$info{'dir'} = $path;
	$info{'name'} = $name;
	$info{'path'} = $full;
	$info{'st'} = $st;
	$info{'size'} = $st->size;
	$info{'mode'} = mode_string $st->mode;
	$info{'mtime'} = time_string $st->mtime;
	$info{'atime'} = time_string $st->atime;
	$info{'ctime'} = time_string $st->ctime;

	my $f_ext = $EXT{$ext};
	$info{'ext'} = $f_ext;

	# [QR] all extension fields.
	map { push(@ext_fields, $_) if m/^[^.]/; } keys %$f_ext;

	return \%info;
}

sub by_name { $a->{'name'} <=> $b->{'name'}; }
sub by_size { $a->{'size'} <=> $b->{'size'}; }
sub by_mode { $a->{'st'}->mode <=> $b->{'st'}->mode; }
sub by_mtime { $a->{'st'}->mtime <=> $b->{'st'}->mtime; }
sub by_ext {
	(my $e_a = $a->{'name'}) =~ s/^.*(\.[^.]+)$/\1/;
	(my $e_b = $b->{'name'}) =~ s/^.*(\.[^.]+)$/\1/;
	$e_a <=> $e_b;
}

sub init {
	list_getopt();

	$path = path_normalize $path;

	$path_pre = "$path$SLASH_CHR";
		# if the root
		$path_pre = $path if ($path =~ m/^$SLASH_PAT$/);
	$path_up = "$path_pre..";

	my $dh = new DirHandle $path;
	while ($_ = $dh->read) {
		# skip . and .. entries.
		next if (m/^\.{1,2}$/);

		# skip hiddens
		next if ((!($opt & $BUTTONS{showhid})) and m/^\./);

		push @fnames, $_;
		push @files, get_file_info;
	}

	my $sort_by = param('sort');
	if ($sort_by eq 'name') 	{ @files = sort by_name @files }
	elsif ($sort_by eq 'size')	{ @files = sort by_size @files }
	elsif ($sort_by eq 'mode')	{ @files = sort by_mode @files }
	elsif ($sort_by eq 'mtime')	{ @files = sort by_mtime @files }
	elsif ($sort_by eq 'ext')	{ @files = sort by_ext @files }
	else { return; }
}

sub main {
	print <<"EOB";
Content-Type: text/html

<html>
	<head>
		<title>Directory of $path_pre</title>
		<script language='JavaScript'>
			function check_all_files(state) {
				var count = formx.elements.length;
				var i;
				for (i = 0; i < count; i++) {
					var obj = formx.elements[i];
					if (obj.name.substring(0, 3) == "chk") {
						obj.checked = state;
					}
				}
			}
		</script>
	</head>
	<body>
	<h1>Directory of $path_pre -
		[<a href="?opt=$opt&path=$path_up">Up</a>]
		[<a href="?opt=$opt&path=">Home</a>]
		[<a href="?opt=$opt&path=$SLASH_CHR">Root</a>]
	</h1>
	<hr>
	<form name='formx' id='formx' action="sel_op.pl" method="get">
		<input type="hidden" name="opt" value="$opt">
		<input type="hidden" name="path" value="$path">
		<table border="0">
		<thead>
			<th colspan="3"><a href="?opt=$opt&sort=mode&path=$path">Mode</a></th>
			<th><a href="?opt=$opt&sort=name&path=$path">Filename</a></th>
			<th><a href="?opt=$opt&sort=size&path=$path">Size</a></th>
			<th><a href="?opt=$opt&sort=mtime&path=$path">Last modified</a></th>
EOB
	foreach (@ext_fields) {
		print "<th>$_</th>\n";
	}
	print "</thead>\n";

	foreach (@files) {
		my ($s_name, $chk_name, $image);
		my $fp = $_->{'path'};
		my $name = $_->{'name'};
		my $is_dir = 0;
		my $is_link = 0;
		my $spec = 0; 		# 1 pipe, 2 block, 3 char

		if ($name =~ m/^[a-zA-Z.0-9]+$/) {
			($s_name = $name) =~ tr/./_/;
			$chk_name = "chk_$s_name";
		} else {
			$s_name = make_id($name);
			$chk_name = "chkI_$s_name";
		}

		$is_link = 1 if (-l $fp);

		if (-d $fp) {
			$image = "mat/folder.gif";
			$is_dir = 1;
		} elsif (-f $fp) {
			$image = "mat/file.gif";
			if (m/\.([a-zA-Z_0-9]+)$/) {
				my $ext = $1;
				my $ext_re = quotemeta "e_$ext";
				map {
					$image = "mat/$_" if m/^$ext_re\./i;
				} @mat_files;
			}
		} else {
			if (-p $fp) {
				$image = "mat/s_pipe.gif";
				$spec = 1;
			} elsif (-b $fp) {
				$image = "mat/s_block.gif";
				$spec = 2;
			} elsif (-c $fp) {
				$image = "mat/s_char.gif";
				$spec = 3;
			} else {
				$image = "mat/s_other.gif";
				$spec = -1;
			}
		}

		local $\ = "\n";
		print "<tr><td>";
		print "<input type='checkbox' name='$chk_name'>";
		print "</td><td>".$_->{'mode'};
		if ($opt & $BUTTONS{open}) {
			if ($is_dir) {
				print "</td><td>"
					."<img border='0' src='as_file.pl?path=$image'></td><td>\n"
					."<a href='?opt=$opt&path=$fp'>$name</a>";
			} else {
				print "</td><td><a href='as_file.pl?path=$fp'>"
					."<img border='0' src='as_file.pl?path=$image'></a></td><td>\n"
					."<a href='$fp'>$name</a>";
			}
		} else {
			print "$name";
		}
		print "</td><td>".$_->{'size'};
		print "</td><td>".$_->{'mtime'};
		print "</td>";

		my $ext = $_->{'ext'};
		foreach (@ext_fields) {
			if ($ext) {
				my $val = $ext->{$_};
				print "<td>";
				print qq(<a href="ext_op.pl?opt=$opt&path=$fp&cmd=$val">$_</a>);
				print "</td>";
			} else {
				print "<td> </td>";
			}
		}

		print "</tr>\n";
	}

	print <<"EOB";
		</table>
		<hr>
		<input type='button' value='All'
			onclick='javascript: check_all_files(true); '>
		<input type='button' value='Clear'
			onclick='javascript: check_all_files(false); '>
		<input type='checkbox' name='recursive' checked
		 >Select sub-directories
		| Operation:
		<select name="sel_op">
EOB

	foreach (@ext_fields) {
		print "<option value='$_'>$_</option>\n";
	}
	print <<"EOB";
			<option value=".modify">Modify</option>
			<option value="-">------------</option>
			<option value=".move">Move to ...</option>
			<option value=".rename">Rename</option>
			<option value=".delete">Delete</option>
			<option value=".chmod">Change mode</option>
			<option value=".zip">Download as zip</option>
			<option value="-">------------</option>
			<option value=".mkdir">Create Folder</option>
			<option value=".mktxt">Create Text File</option>
			<option value=".upload">Upload file</option>
		</select>
		<input type="submit" value="Next">
	</form>
	<hr>
	<center>
		Mini Web Console (Under develop) <em>Powered by Perl</em><br>
		Author: <a href="mailto:jljljjl\@yahoo.com?subject=[webcon]">Danci.Z</a>
	</center>
	</body>
</html>
EOB
}

&init;

my $view_overload = $path_pre.$WConf{'listdir_overload'};
if (-f $view_overload) {
	require $view_overload;
} else {
	&main;
}
