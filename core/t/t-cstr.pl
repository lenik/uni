
use strict;

use cmt::cstr;
use Data::Dumper;

my $a = 'hello';
my $b = 'world';

my $c;
cs_comp $c => [ \$a, \$b ], ':';

sub dmp {
    print "A=$a, B=$b, C=$c\n";
}

dmp;

$a = 'new-A'; dmp;
$b = 'new-B'; dmp;
$c = 'Well:Done:OK'; dmp;
$a .= '---'; dmp;
