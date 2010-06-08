#! /usr/bin/perl -w
#
# $Id$
#

use strict;
use Pod::Usage;

=head1 NAME

B<svn-merge-repos.pl> - Merge SVN repositories into one, in rev/date order

=head1 SYNOPSIS

svn-merge-repos [options] -t repos[/subdir] repos[/ssubdir]:tsubdir...

=head1 OPTIONS

=over 4

=item B<--help> or B<-h>

This help.

=item B<--man> or B<-m>

More help.

=item B<--svn=/path/to/svn> or B<-s cmd>

Use this 'svn' command.

=item B<--svnadmin=/path/to/svnadmin> or B<-a cmd>

Use this 'svnadmin' command.

=item B<--svndumpfilter=/path/to/svndumpfilter> of B<-d cmd>

Use this 'svndumpfilter' command.

=item B<--target=repos_path/subdir> or B<-t path/dir>

Path to destination repository, possibly within the specified
sub directory. You cannot merge several repositories within the
very same destination directory.

=item B<--source>

Add a 'merge:source' revision property to tell the revision source.
Revision property changes must be allowed on the target repos in order
to do so.

=item B<--verbose> or B<-v>

Be verbose. Repeat for more.

=item B<--version>

Show script revision and exit.

=back

=head1 ARGUMENTS

Arguments are of the form 'repos_path/ssubdir:tsubdir'.
The source sub-directory 'ssubdir' of the source repository is merged into
the specified target sub directory 'subdir/tsubdir' in the target repository.

=head1 NOTES

Revisions are exported and imported thanks to the 'svnadmin' command.
This means that an actual filesystem path to the repository is necessary.
URL-based subversion repository path will not work.

The sorting is based on revision numbers within a given source, and
on dates between different sources.  The resulting merged revisions are
not fully sorted with respect to dates, as other prior commits in the same
repository keep their original dates.

Do not merge big and pretty independent repositories.
Consider using 'svn:external' properties prior to merging repositories.

To split a repository, 'svnadmin dump' + 'svndumpfilter include'
are your friends.

It is a very bad idea to use the repository while the merge is in progress.

Do not trust hardware. Do not trust software either. Always backup your data,
especially before running any automatic thing such as this script.

=head1 EXAMPLE

  sh> svn-merge-repos.pl -t ./proj ./foo/trunk:proj1 ./bla/trunk:proj2

Merge "trunk" of repositories "foo" and "bla" as subdirectories "proj1"
and "proj2" in repository "proj".

=head1 BUGS

All softwares have bugs, this is a software, hence...

The documentation is scarce.

I'm unsure about what happens with svn copies. Just merge your trunk?

Dates are in some timezone. What happens around daylight time changes?

The sorting breaks around year 10000. Shame on me, but I'll be dead by then.

=head1 LICENSE

=for html
<img src="http://www.gnu.org/graphics/gplv3-127x51.png"
alt="GNU GPLv3" align="right" />

(c) Fabien COELHO <fabien at coelho dot net> 2006-2009
L<http://www.coelho.net/>.

This is free software, both inexpensive and available with sources.
The GNU General Public License v3 or more applies (GPLv3+).
The brief summary is: You get as much as you paid for, and I am not
responsible for anything.
See L<http://www.gnu.org/copyleft/gpl.html> for details.

If you are very happy with this software, I would appreciate if you could
send a postcard mentioning it (see my web page for current address).

=head1 DOWNLOAD

The latest version of the script is available at
L<http://www.coelho.net/svn-merge-repos.pl>.

=head1 SEE ALSO

B<Subversion> at L<http://subversion.tigris.org/>.

B<svndumptool> at L<http://svn.borg.ch/svndumptool/>,
by Martin Furter: a set of python classes and a command to manipulate
svn dump files.
Although it may be less straightforward, it should do a better job
especially if you want to reorganize 'branches' and 'tags' directories.

B<svnfusion> at L<http://svnfusion.sourceforge.net>.
by Marcos Mayorga: a bash script which merges full repositories, with
few options available.

=cut

# svn revision number
my $rev = '$Rev: 620 $';
$rev =~ tr/0-9//cd;

####################################################################### OPTIONS

my $svn = 'svn';
my $svnadmin = 'svnadmin';
my $svndumpfilter = 'svndumpfilter';
my $target = undef;
my $verb = 0;
my $source = 0;

use Getopt::Long;
GetOptions("target|t=s" => \$target,
	   "svndumpfilter|dumpfilter|d=s" => \$svndumpfilter,
	   "svnadmin|admin|a=s" => \$svnadmin,
	   "svn|s=s" => \$svn,
	   "verbose|v+" => \$verb,
	   "source" => \$source,
	   "help|h" => sub { pod2usage(-verbose => 1); },
	   "man|m" => sub { pod2usage(-verbose => 2); },
	   "version" => sub { print "$0 revision $rev\n"; exit 0; })
    or die "invalid option ($!)";

die 'expecting target option' unless $target;

##################################################################### FUNCTIONS

# safe execute system
sub sys($)
{
    my ($cmd) = @_;
    print STDERR "sys($cmd)\n" if $verb;
    system($cmd) and die "cmd=$cmd\n$!";
}

# simple quote string for shell
# I'm unsure about what happens with non ascii encodings.
sub quote($)
{
    my ($str) = @_;
    $str =~ s/([\'\\])/\\$1/g;
    return "'$str'";
}

# is argument an svn repository?
sub is_repository($)
{
    my ($path) = @_;
    return -d $path and -f "$path/README.txt" and -f "$path/format"
	and -d "$path/db" and -d "$path/hooks" and -d "$path/locks"
	and -d "$path/conf";
}

# return separated repository path and sub directory from path
sub repository_path($)
{
    my ($path) = @_;
    my ($dir, $subdir) = ($path, '');
    while ($dir and not is_repository($dir) and $dir =~ m,(.*)/([^/]*)$,)
    {
	($dir, $subdir) = ($1, $subdir? "$2/$subdir": $2);
    }
    die "path '$path' not a repository" unless is_repository($dir);
    return ($dir, $subdir);
}

################################################################## DO SOMETHING

my ($target_repos, $target_subdir) = repository_path($target);
die "no such repository: $target_repos" unless -d $target_repos;

# %sources: full source path -> subdir map in target repository
# %dirs: subdir map in target repository -> full source path
# %srcrepos: full source path -> source repository path
# %srcinclude: full source path -> include source directory
# %number: full source path -> argument number starting from 0
my (%sources, %dirs, %srcrepos, %srcinclude, %number) = ();
my $argnum = 0;

# process sources specifications repos-path:sub/dir
for my $src (@ARGV)
{
    die "invalid source specification: $src" unless $src =~ /(.*):(.*)/;
    my ($path,$dir) = ($1,$2);
    $dir = "$target_subdir/$dir" if $target_subdir;
    my ($path_repos, $path_include) = repository_path($path);
    # would it be possible to merge two paths together?
    die "repository $path is already taken" if exists $sources{$path};
    # prefixes should also be forbidden
    die "directory $dir is already taken" if exists $dirs{$dir};
    die "must used relative path for $2" if $dir =~ /^\//;
    # directory <-> repository
    $sources{$path} = $dir;
    $dirs{$dir} = $path;
    $srcrepos{$path} = $path_repos;
    $srcinclude{$path} = $path_include? "/$path_include": '';
    $number{$path} = $argnum++;
}

# get all log history
my (@all, %data);
for my $path (sort keys %sources)
{
    my $dir = $sources{$path};
    open LOG, "$svn log --quiet " . quote("file://$path") . " |" or die $!;
    while (<LOG>)
    {
	chomp;
	next if /^-+$/;
	# hmmm... the date is in some timezone. Could force UTC somehow?
	my ($rev, $date) = (split / \| /)[0,2];
	$rev =~ s/^r//;
	$date =~ tr/0-9//cd;
	$date =~ s/^(\d{14}).*/$1/; # YYYYMMDDHHMMSS format
	my $id = "$dir:$rev";

	push @all, $id;
	$data{$id} = "$date:$rev:$number{$path}";
    }
    close LOG or die $!;
}

# create directories within target repository
for my $dirpath (sort keys %dirs)
{
    my $path = '';
    for my $dir (split '/', $dirpath)
    {
	$path .= "/$dir";
	sys("$svn ls " . quote("file://$target_repos$path") . " || " .
	    "$svn mkdir --message " . quote("$0: mkdir $path") .
	    " " . quote("file://$target_repos$path"));
    }
}

# compare revisions: revision number if same path, otherwise use date
sub cmp_rev($$)
{
    my ($a,$b) = @_;
    my ($da,$ra,$pa) = split(/:/, $data{$a});
    my ($db,$rb,$pb) = split(/:/, $data{$b});
    return $pb eq $pa? $ra <=> $rb: $da cmp $db;
}

# get them sorted by date
# the sorting will break around year 10000.
for (sort cmp_rev @all)
{
    my ($dir,$rev) = split(/:/);
    print STDERR "considering dir=$dir rev=$rev\n" if $verb;
    my $path = $dirs{$dir};
    my $repospath = $srcrepos{$path};

    sys(# dump wanted revision from source repository
	"$svnadmin dump --revision $rev --incremental " . quote($repospath) .
	# pass through optionnal filter
	($srcinclude{$path} ?
	 " | $svndumpfilter include " . quote($srcinclude{$path}): '') .
	# load into target repository
	" | $svnadmin load --parent-dir " . quote($dir) .
	" " . quote($target_repos));

    # store source as a rev prop
    sys("$svn pset merge:source " .
	quote("file://$repospath$srcinclude{$path}\@$rev") .
	" --revprop --revision HEAD " . quote("file://$target_repos"))
	if $source;
}
