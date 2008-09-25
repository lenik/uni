#!/usr/bin/perl

$| = 1;

for (my $i = 0; $i < 100; $i++) {
	print STDOUT "This is from output $i\n";
#	print STDOUT "This is from output $i again\n";
	print STDERR "This is from error $i\n";
#	print STDERR "This is from error $i again\n";
	$_ = <>;
	chomp;
	last if ($_ eq 'end');
	print "Echo[$i]: $_\n";
}
