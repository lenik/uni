
package cmt::util;

use strict;
use POSIX;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub datetime {
    return strftime('%Y-%m-%d %H:%M:%S', localtime);
}

@ISA = qw(Exporter);
@EXPORT = qw(
	datetime
	);

1;
