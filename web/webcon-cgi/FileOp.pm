
package webcon::FileOp;

use strict;
use CGI qw/:standard *table/;
use FileHandle;
use DirHandle;
use webcon::supp::Fcntl qw/:mode/;
use webcon::FileBase;
use Exporter;
use vars qw/@ISA @EXPORT/;
use vars qw/$path $path_pre @fnames $fcount/;

sub v_modify {}
sub f_modify {}


sub v_move {
	print <<"EOB";
		Target directory:
		<input type='text' size='50' name='target'>
EOB
	}
sub v_rename {
	print <<"EOB";
		<p><input type='radiobox' name='rmethod' value='simple'>By simple variable-substituation: <br>
		Re-name as: <input type='text' size='50' name='sim_name' value='$,2n-1$x'>

		<p><input type='radiobox' name='rmethod' value='regex'>By regular-expression: <br>
		Match part: <br>
		<input type='text' size='50' name='re_from' value='^(.*)(\\.[^.]*)?\$'><br>
		Substitute part: <br>
		<input type='text' size='50' name='re_to' value='My\\1\\2'><br>
EOB
	}
sub v_delete {
	print <<"EOB";
		<input type='checkbox' name='recursive'>
			Delete sub-directories recursive
EOB
	}
sub v_chmod {
	print <<"EOB";
		<input type='checkbox' name='mod_RU' checked>Owner user can read<br>
		<input type='checkbox' name='mod_WU' checked>Owner user can write<br>
		<input type='checkbox' name='mod_XU' checked>Owner user can execute<br>
		<input type='checkbox' name='mod_RG' checked>Owner Group can read<br>
		<input type='checkbox' name='mod_WG' checked>Owner Group can write<br>
		<input type='checkbox' name='mod_XG' checked>Owner Group can execute<br>
		<input type='checkbox' name='mod_RO' checked>Other users can read<br>
		<input type='checkbox' name='mod_WO' checked>Other users can write<br>
		<input type='checkbox' name='mod_XO' checked>Other users can execute<br>
		<br>
		<input type='checkbox' name='recursive'>Recurse to sub-directories<br>
EOB
	}
sub v_zip {
	print <<"EOB";
		<select name='zip_method'>
			<option value='deflated'>Deflated</option>
			<option value='stored'>Stored</option>
		</select>
		<input type='checkbox' name='recursive'>Contain sub-directories<br>
EOB
	}
sub v_mkdir {
	print <<"EOB";
		New folder name:
		<input type='text' name='dir_name' size='50' value='new-folder'>
EOB
	}
sub v_mktxt {
	print <<"EOB";
		<center>
		<p>Text file name:
			<input type='text' name='txt_file' size='50' value='newtext.txt'>
		</p><p>Type plain text here: <br>
		<textarea name='txt_body' cols='80' rows='25'
		>Hello, world!</textarea>
		</p><p>
		You should type ASCII in the text box,
		and you can specify a different encoding method for saving:
		<select name='save_charset'>
			<option value='iso_1'>ISO-8859-1</option>
			<option value='gb2312'>Simplified Chinese</option>
			<option value='big5'>Traditional Chinese</option>
			<option value='utf-8'>UTF-8</option>
			<option value='unicode'>Unicode</option>
		</select>
		</p></center>
		<br>
EOB
	}
sub v_upload {
	print <<"EOB";
		Upload file: <br>
		<input type='file' name='upload_file' size='50'><br>
		Rename as (filename only, don't include directory name): <br>
		<input type='text' name='new_name' size='50'><br>
EOB
	}

sub f_move {
	my $succ = 0;
	foreach (@fnames) {
		my $from = $path_pre.$_;
		my $target = param('target');
		my $target_pre = $target.$SLASH_CHR;
			$target_pre = $target if (substr($target, -1) =~ m/$SLASH_PAT/);
		my $to = $target_pre.$_;
		$succ++ if (rename $from, $to);
	}
	return $succ == $fcount ? 0 : $fcount - $succ;
}
sub f_rename {
	my $succ = 0;
	my $re_match = param('re_match');
	my $re_sub = param('re_sub');
	foreach (@fnames) {
		my $from = $_;
		(my $to = $from) =~ s/$re_match/$re_sub/;
		$from = $path_pre.$from;
		$to = $path_pre.$to;
		$succ++ if (rename $from, $to);
	}
	return $succ == $fcount ? 0 : $fcount - $succ;
}
sub do_delete {
	my ($path, $recur) = @_;
	print "--> $path\n";
	my $succ;
	if (-d $path) {
		if ($recur) {
			my ($dh, @list);
			my ($sub_total, $sub_succ) = (0, 0);
			my $path_pre = $path.$SLASH_CHR;
				$path_pre = $path if (substr($path, -1) =~ m/$SLASH_PAT/);
			$dh = new DirHandle $path;
			@list = $dh->read;
			foreach (@list) {
				next if (m/^\.{1,2}$/); 	# skip ./..
				my $sub_path = $path_pre.$_;
				$sub_total++;
				$sub_succ++ if (do_delete($sub_path, $recur) == 0);
			}

			# some sub-items can't be removed.
			if ($sub_succ != $sub_total) {
				return $sub_total - $sub_succ;
			}
		}
		$succ = rmdir($path);
		return $succ ? 0 : -1;
	} else {
		$succ = unlink($path);
		return $succ ? 0 : -1;
	}
}
sub f_delete {
	my $succ = 0;
	my $recur = param('recursive');
	foreach (@fnames) {
		my $full = $path_pre.$_;
		$succ++ if 0 == do_delete($full, $recur);
	}
	return $succ == $fcount ? 0 : $fcount - $succ;
}
sub do_chmod {
	my ($file, $mode, $recur) = @_;
	my $ret;
	$ret = chmod($file, $mode);
	if ($recur && -d $file) {
		my $dh = new DirHandle $file;
		my @sublist = $dh->read;
		foreach (@sublist) {
			my $sub_name = $file.$SLASH_CHR.$_;
			my $sub_ret = do_chmod($sub_name, $mode, $recur);
			$ret &&= $sub_ret;
		}
	}
	return $ret;
}
sub f_chmod {
	my $new_mode = 0;
	my $recur = param('recursive');
	my $succ = 0;
	$new_mode |= &S_IRUSR if param('mod_RU');
	$new_mode |= &S_IWUSR if param('mod_WU');
	$new_mode |= &S_IXUSR if param('mod_XU');
	$new_mode |= &S_IRGRP if param('mod_RG');
	$new_mode |= &S_IWGRP if param('mod_WG');
	$new_mode |= &S_IXGRP if param('mod_XG');
	$new_mode |= &S_IROTH if param('mod_RO');
	$new_mode |= &S_IWOTH if param('mod_WO');
	$new_mode |= &S_IXOTH if param('mod_XO');

	foreach (@fnames) {
		my $full = $path_pre.$_;
		$succ++ if (do_chmod($full, $new_mode, $recur));
	}
	return $succ == $fcount ? 0 : $fcount - $succ;
}
sub f_zip {
1}
sub f_mkdir {
	my $dir_name = $path_pre.param('dir_name');
	if (mkdir $dir_name, 0777) {
		return 0;
	} else {
		return -1;
	}
}
sub f_mktxt {
	my $filename = $path_pre.param('txt_file');
	my $text = param('txt_body');
	my $encode = param('save_charset');

	if (open(TXT_FILE, ">$filename")) {
		print TXT_FILE $text;
		close TXT_FILE;
		return 0;
	} else {
		return -1;
	}
}
sub f_upload {
	my $upload_file = param('upload_file'); 	# remote path
	my ($remote_name) = ($upload_file =~ 		# remote name
		m/(?:^|$SLASH_PAT)([^$SLASH_PAT]*)$/);
	my $local_name = param('new_file'); 		# local name
		# if not specified, use original name from remote.
		$local_name ||= $remote_name;

	my $fremote = upload('upload_file');
	if (!$fremote) {
		# user may click [Stop] or some unexpected error happens.
		return -1;
	}

	my $flocal = new FileHandle ">$path_pre$local_name";
	if (!$flocal) {
		# can't open local file for write.
		return -1;
	}

	my $buf;
	while ($fremote->read($buf, 10, 1) != 0) {
		print $flocal substr($buf, 1);
	}
	return 0;
}


@ISA = qw(Exporter);
@EXPORT = qw(
	$path $path_pre @fnames $fcount
	v_move v_rename v_delete v_chmod v_zip v_mkdir v_mktxt v_upload
	f_move f_rename f_delete f_chmod f_zip f_mkdir f_mktxt f_upload
	);

1;
