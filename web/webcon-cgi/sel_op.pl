#!/usr/bin/perl -I../

package webcon::shell;

use strict;
use CGI qw/:standard *table/;
use DirHandle;
use webcon::FileBase;
use webcon::FileOp;


my %commands = (
	'.modify'	=> ['Modify text files in-place',
					\&v_modify, \&f_modify,
					],
	'.move'		=> ['Move files to another directory',
					\&v_move, \&f_move,
					],
	'.rename'	=> ['Rename files by perl regular-expression',
					\&v_rename, \&f_rename,
					],
	'.delete'	=> ['Delete files',
					\&v_delete, \&f_delete,
					],
	'.chmod'		=> ['Change mode of files',
					\&v_chmod, \&f_chmod,
					],
	'.zip'		=> ['Download as .zip file',
					\&v_zip, \&f_zip,
					],
	'.mkdir'		=> ['Create new folder',
					\&v_mkdir, \&f_mkdir,
					],
	'.mktxt'		=> ['Create new text file',
					\&v_mktxt, \&f_mktxt,
					],
	'.upload'	=> ['Upload remote files',
					\&v_upload, \&f_upload,
					],
	);

my ($opname, $cmd);
my ($opt, $sort);

sub err_page {
	my ($info, $errno) = @_;
	$errno ||= 500;
	print <<"EOB";
Status: $errno $info

EOB
	exit $errno;
}

sub init {
	$path = param('path');
	$path_pre = $path.$SLASH_CHR;
		$path_pre = $path if (substr($path, -1) =~ m/$SLASH_PAT/);
	$opt = param('opt');
	$sort = param('sort');

	$opname = param('sel_op');
	if (!($cmd = $commands{$opname})) {
		err_page "Invalid operation specified: $opname";
	}
	# param-> chk_??? ==> @fnames

	my %params = %$CGI::Q;
	my ($dh, @list);
	$dh = new DirHandle $path;
	@list = $dh->read;

	foreach (keys %params) {
		if (s/^chk_(.*)$/\1/) {
			y/_/./;
			push @fnames, $_;
		} elsif (s/^chkI_(.*)$/\1/) {
			my $n = restore_text($_, \@list);
			push @fnames, $n;
		}
	}

	foreach (param('files')) {
		push @fnames, $_;
	}
	$fcount = scalar(@fnames);
}

sub main_pre {
	my $fcount = scalar(@fnames);

	print <<"EOB";
Content-Type: text/html

<html>
	<head>
		<title>$cmd->[0]</title>
	</head>
	<body>
		<h1>$cmd->[0]</h1>
		<hr>
		<form action="sel_op.pl" method="get">
			<input type='hidden' name='path' value='$path'>
			<input type='hidden' name='opt' value='$opt'>
			<input type='hidden' name='sort' value='$sort'>
			<input type='hidden' name='sel_op' value='$opname'>
			<p>Following $fcount files will be affected: </p>
			<ul>
EOB

	foreach (@fnames) {
		print "<li>$_</li><input type='hidden' name='files' value='$_'>\n";
	}
	print "</ul><hr>\n";

	my $subx = $cmd->[1];
	my $code = $subx ? &$subx : 0;

	print <<"EOB";
			<p>Proceed the operation? </p>
			<input type='submit' name='proceed' value='Yes'>
			<input type='submit' name='cancel' value='No'>
		</form>
	</body>
</html>
EOB
}

sub main_back {
	print <<"EOB";
Content-Type: text/html

	<html><body><form name='formx' id='formx' action="listdir.pl" method="get">
		<input type='hidden' name='path' value='$path'>
		<input type='hidden' name='opt' value='$opt'>
		<input type='hidden' name='sort' value='$sort'>
		<h2>
			If this page didn't automaticly go back,
			please click this button:
		</h2><br>
		<input type='submit' value='Immediately Go Back'>
	</form>
	<script language='JavaScript'>
		formx.submit();
	</script>
	</body></html>
EOB
}

sub main_proc {
	print <<"EOB";
Content-type: text/html

<html>
	<head>
		<title>$cmd->[0]</title>
	</head>
	<body>
		<h1>Processing: $cmd->[0]</h1>
		<hr>
		Executing request, please wait...<BR>
EOB

	my $code = &{$cmd->[2]};
	my $util_code = '';
	if ($code == 0) {
		$util_code = "<script language='JavaScript'>formx.submit();</script>";
	}
	print << "EOB";
		Completed with exit code($code) ! <BR>

		<form name='formx' id='formx' action="listdir.pl" method="get">
			<input type='hidden' name='path' value='$path'>
			<input type='hidden' name='opt' value='$opt'>
			<input type='hidden' name='sort' value='$sort'>
			Click here to return to browse view:
			<input type='submit' value='Return'>
			$util_code
		</form>
	</body>
</html>
EOB
}

&init;

if (param('proceed') eq 'Yes') {
	&main_proc;
} elsif (param('cancel') eq 'No') {
	&main_back;
} else {
	&main_pre;
}
