
package webcon::supp::Fcntl;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT %EXPORT_TAGS/;


my %FCNTL = (
	FDIR		=> 40000,
	FCHR		=> 20000,

	FBLK		=> 0x80000000, #0
	FLNK		=> 0x80000000, #0
	FPIPE		=> 0x80000000, #0
	FSOCK		=> 0x80000000, #0

	SUID		=> 04000,
	SGID		=> 02000,
	SVTX		=> 01000,
	RUSR		=> 00400,
	WUSR		=> 00200,
	XUSR		=> 00100,
	RGRP		=> 00040,
	WGRP		=> 00020,
	XGRP		=> 00010,
	ROTH		=> 00004,
	WOTH		=> 00002,
	XOTH		=> 00001,
);

sub S_ISUID() { $FCNTL{SUID} }
sub S_ISGID() { $FCNTL{SGID} }
sub S_ISVTX() { $FCNTL{SVTX} }
sub S_IRUSR() { $FCNTL{RUSR} }
sub S_IWUSR() { $FCNTL{WUSR} }
sub S_IXUSR() { $FCNTL{XUSR} }
sub S_IRGRP() { $FCNTL{RGRP} }
sub S_IWGRP() { $FCNTL{WGRP} }
sub S_IXGRP() { $FCNTL{XGRP} }
sub S_IROTH() { $FCNTL{ROTH} }
sub S_IWOTH() { $FCNTL{WOTH} }
sub S_IXOTH() { $FCNTL{XOTH} }

sub S_IREAD()  { $FCNTL{RUSR} | $FCNTL{RGRP} | $FCNTL{ROTH} }
sub S_IWRITE() { $FCNTL{WUSR} | $FCNTL{WGRP} | $FCNTL{WOTH} }
sub S_IEXEC()  { $FCNTL{XUSR} | $FCNTL{XGRP} | $FCNTL{XOTH} }

sub S_IFDIR()  { $FCNTL{FDIR} }
sub S_IFCHR()  { $FCNTL{FCHR} }
sub S_IFBLK()  { $FCNTL{FBLK} }
sub S_IFLNK()  { $FCNTL{FLNK} }
sub S_IFPIPE() { $FCNTL{FPIPE} }
sub S_IFSOCK() { $FCNTL{FSOCK} }

sub S_ISDIR($) { my ($mode) = @_; $mode & &S_IFDIR; }
sub S_ISCHR($) { my ($mode) = @_; $mode & &S_IFCHR; }
sub S_ISBLK($) { my ($mode) = @_; $mode & &S_IFBLK; }
sub S_ISLNK($) { my ($mode) = @_; $mode & &S_IFLNK; }
sub S_ISPIPE($) { my ($mode) = @_; $mode & &S_IFPIPE; }
sub S_ISSOCK($) { my ($mode) = @_; $mode & &S_IFSOCK; }


@ISA = qw(Exporter);
@EXPORT = qw(
	S_ISUID S_ISGID S_ISVTX
	S_IRUSR S_IWUSR S_IXUSR S_IRGRP S_IWGRP S_IXGRP
	S_IROTH S_IWOTH S_IXOTH
	S_IFDIR S_IFCHR S_IFBLK S_IFLNK S_IFPIPE S_IFSOCK
	S_ISDIR S_ISCHR S_ISBLK S_ISLNK S_ISPIPE S_ISSOCK
	S_IREAD S_IWRITE S_IEXEC
	);
%EXPORT_TAGS = (
	'mode'		=>
		[qw(
			S_ISUID S_ISGID S_ISVTX
			S_IRUSR S_IWUSR S_IXUSR S_IRGRP S_IWGRP S_IXGRP
			S_IROTH S_IWOTH S_IXOTH
			S_IFDIR S_IFCHR S_IFBLK S_IFLNK S_IFPIPE S_IFSOCK
			S_ISDIR S_ISCHR S_ISBLK S_ISLNK S_ISPIPE S_ISSOCK
			S_IREAD S_IWRITE S_IEXEC
		)],
	);

1;
