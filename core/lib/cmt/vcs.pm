
# Common Module for dir-T

package cmt::vcs;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;


sub parse_id {
    my ($id) = @_;
    $id ||= '$Id: vcs.pm,v 1.3 2005-01-06 04:21:47 dansei Exp $';
    my @segs = $id =~ m/
        ^ \$ [I][d][:] \s (.*?) \s ([0-9.]+) \s ([0-9\/\\\-]+) \s
             ([0-9:]+) \s (.*?) \s (\w+) \s \$ $
        /x;
    my %info = (
            'rcs' => $segs[0],
            'rev' => $segs[1],
            'date' => $segs[2],
            'time' => $segs[3],
            'author' => $segs[4],
            'state' => $segs[5],
        );
    return %info;
}


@ISA = qw(Exporter);
@EXPORT = qw(
	parse_id
	);
1;
