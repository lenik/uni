
package cmt::util;

use strict;
use DirHandle;
use cmt::path;
use Exporter;
use vars qw/@ISA @EXPORT/;


    sub scanDirectory {
        my ($dir, $recursive, $callback, $user) = @_;
        my $dh = new DirHandle($dir) || return;

        my @files = $dh->read;
        $dh->close;
        for (@files) {
            next if (m/^\.+$/);
            my $path = "$dir/$_";
            if (-d $path) {
                if ($recursive) {
                    scanDirectory($path, $recursive, $callback, $user);
                }
            }
            # for each file or directory
            &$callback($path, $user);
        }
    }


@ISA = qw(Exporter);
@EXPORT = qw(
	scanDirectory
	);

1;
