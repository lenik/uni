# COPY OF cygwin-1.7.0-30

# The  file fstab contains descriptive information about the various file
# systems.  fstab is only read by programs, and not written;  it  is  the
# duty  of  the system administrator to properly create and maintain this
# file.  Each filesystem is described on a separate line; fields on  each
# line are separated by tabs or spaces.  Lines starting with '#' are com-
# ments.
#
# The first field, (fs_spec),  describes  the  block  special  device  or
# remote filesystem to be mounted.  On Cygwin, this is the native Windows
# path which  the mount point links in.  As path separator you MUST use a
# slash.  Usage of  a backslash might  lead to  unexpected  results.  UNC
# paths  (using  slashes,  not  backslashes)  are  allowed.  If  the path
# contains spaces these can be escaped as '\040'.
#
# The second field, (fs_file), describes the mount point for the filesys-
# tem.  If the  name  of  the  mount point contains  spaces these can be
# escaped as '\040'.
#
# The third field, (fs_vfstype), describes the type  of  the  filesystem.
# Cygwin supports any string here, since  the file system type is usually
# not  evaluated.  The  noticable  exception  is  the  file  system  type
# cygdrive.  This  type is  used to  set the  cygdrive  prefix.  See  the
# user's guide for more information about the cygdrive prefix.
#
# The  fourth  field, (fs_mntops), describes the mount options associated
# with  the filesystem.  It is  formatted as a  comma separated  list  of
# options.  It contains at least  the type of mount (binary or text) plus
# any additional options appropriate to  the filesystem type.  Recognized
# options  are acl,  noacl, binary,  text, nouser,  user, exec,  notexec,
# cygexec, nosuid, posix=[0|1].  For a description of the options see the
# user's guide.  Note that  nouser mount  points are  not overridable  by
# a later call to mount(2).  This is only possible for user mount points.
# Mount points  are by default acl,binary,nouser mount points, unless you
# specify otherwise.
#
# The fifth (fs_freq) and sixth (fs_passno) field  are ignored.  They are
# so far only specified to keep a Linux-like fstab file layout.
#
# Note that  you don't have  to specify an fstab  entry for the root dir,
# unless you want  to have the  root dir pointing  to somewhere  entirely
# different (hopefully  you know what  you're doing), or  if you want  to
# mount the root dir with special options (for instance, as text mount).
#
# Example entries:
#
#   Just a normal mount point:
#
#       c:/foo /bar fat32 binary 0 0
#
#   A mount point for a textmode mount with case sensitivity switched off:
#
#       C:/foo /bar/baz ntfs text,posix=0 0 0
#
#   A mount point for a Windows directory with spaces in it:
#
#       C:/Documents\040and\040Settings /docs ext3 binary 0 0
#
#   A mount point for a remote directory without ACL support:
#
#       //server/share/subdir /srv/subdir smbfs binary,noacl 0 0
#
#   This is just a comment:
#
#       # This is just a comment
#
#   Set the cygdrive prefix to /mnt:
#
#       none /mnt cygdrive binary 0 0
#

$CYGWIN_ROOT        /               some_fs binary 0 0
$CYGWIN_ROOT/bin    /usr/bin        some_fs binary 0 0
$CYGWIN_ROOT/lib    /usr/lib        some_fs binary 0 0
$HOME               /home           some_fs binary 0 0
$TEMP               /tmp            some_fs binary 0 0
$LAPIOTA            $LAM_KALA       some_fs binary 0 0

# This is default anyway:
none                /mnt            cygdrive binary,posix=0,user 0 0
