#!/usr/bin/perl -I../

package webcon::shell;

use strict;
use FileHandle;
use CGI qw/:standard/;
use webcon::FileBase;
use webcon::fext::Config;


my $path = param('path');
my $ext = param('ext_as');

if (not defined $ext) {
	if ($path =~ m/\.([^.$SLASH_SET]*)$/) {
		$ext = $1;
	}
}

sub err_page {
	my ($msg, $ret) = @_;
	print <<"EOB";
Content-type: text/html

<html><head><title>General Error</title></head><body>
<h1>$msg</h1><hr></body></html>
EOB
	exit $ret;
}

err_page("Invalid parameters specified. ", -1) 		if (!$path || !$ext);
err_page("The specified path must be a file. ", -2)	if (not -f $path);

# otherwise, treat as binary file for download
my $content_type = mime_type($ext);

(my $FH = new FileHandle "$path") || err_page("Can't open file $path");
my $buf;

print "Content-type: $content_type\n";
print "\n";

# block size = 100 bytes, the read() returns bytes actually read.
while ($FH->read($buf, 100) != 0) {
	print $buf;
}
