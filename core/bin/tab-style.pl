#!/usr/bin/perl

use strict;
use Getopt::Long;

my $old_tab = 8;
my $new_tab = 4;
my $prefer_tab = 0;
my $tabify_min = 2;

sub help();


GetOptions(
	'old=i' 	=> \$old_tab,
	'new=i' 	=> \$new_tab,
	'tab' 		=> sub { $prefer_tab = 1 },
	'space' 	=> sub { $prefer_tab = 0 },
	'mintab=i'	=> \$tabify_min,
	'help'		=> \&help,
	);

$old_tab>0 or die 'tab-size must be positive number';
$new_tab>0 or die 'tab-size must be positive number';

# print "$old_tab -> $new_tab\n";


while (<>) {
	my @chars = split('', $_);
	my ($i, $x) = (0, 0);

	foreach my $c (@chars) {
		if ($c eq "\t") {
			$c = ' 'x($old_tab-$x);
			$chars[$i] = $c;
			$x = 0;
		} else {
			$x = ($x+1) % $old_tab;
		}
		$i++;
	}

	$_ = join('', @chars);

	if ($prefer_tab) {
		my @segs;
		while (m/.{1,$new_tab}/sg) {
			#assert no '\t' inside.
			my $seg = $&;
			if ($seg =~ m/( +)$/) {
				if (length($1) >= $tabify_min) {
					$seg =~ s/( +)$/\t/;
				}
			}
			print "$seg";
		}
	} else {
		print $_;
	}
}

sub help() {
	print <<"EOM";
[TABSTYLE] Adjust tab-style of plain text
Syntax
	$0 --help --tab --space
		--old=<old tab size, default is 8>
		--new=<new tab size, default is 4>
		--mintab=<min spaces for tabify, default is 2>

Version 1
License
	This program is distributed under GPL License.
EOM
	exit 0;
}
