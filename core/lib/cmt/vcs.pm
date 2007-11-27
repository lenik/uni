
# Common Module for dir-T

package cmt::vcs;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;


sub parse_id {
    my ($id) = @_;
    #        0    1      2   3          4         5     6
    $id ||= '$Id$';
    my @segs = split(/\s+/, $id);
    #=~ m/^ \$ [I][d][:] \s (.+?) \s ([0-9.]+) \s ([0-9\/\\\-]+) \s
    #        ([0-9:]+) \s (.*?) \s (\w+) \s \$ $/x;
    my %info = (
            'rcs' => $segs[1],
            'rev' => $segs[2],
            'date' => $segs[3],
            'time' => $segs[4],
            'author' => $segs[5],
            'state' => $segs[6],
        );
    return %info;
}


@ISA = qw(Exporter);
@EXPORT = qw(
	parse_id
	);
1;
