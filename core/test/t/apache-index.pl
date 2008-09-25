use strict;
use Data::Dumper;
use Parse::Apache::Index;

my @files = listdir_http $ARGV[0];
print Dumper(\@files);
