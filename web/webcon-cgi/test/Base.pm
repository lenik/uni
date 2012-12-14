
package webcon::test::Base;

use strict;
use Test::Simple;
use webcon::Config;
use Exporter;
use vars qw/@ISA @EXPORT/;


sub test_batch {
	my ($batch) = @_;
	my $batch_total = scalar(@$batch);
	my $batch_index = 1;
	my $batch_ok = 0;
	my $c_total = 0;
	my $c_ok = 0;

	foreach (@$batch) {
		my $type = $_->{'type'};
		my $name = $_->{'name'};
		my $entry = $_->{'entry'};
		my $cases = $_->{'cases'};
		my $total = scalar(@$cases)/2;
		my $ok = 0;

		print "UNIT>> $name ($total cases)\n";

		my ($args) = $entry =~ s/(\(.*\))//;
		if ($args) {
			$args =~ s/\((.*)\)/$1/; 		# remove '(' and ')'
			$args =~ s/^\s*(.*?)\s*$/$1/; 	# trim space
			$args = length($args); 			# get args count
		} else {
			$args = 0;
		}

		my $case_index = 0;
		for (; $case_index < $total; $case_index++) {
			my $input = $cases->[$case_index*2];
			my $expect = $cases->[$case_index*2+1];
			my $out;
			my $numhead = "$batch_index.".(1+$case_index)."/$total";

			print $numhead.(' 'x(10-length($numhead)));
			$out = eval('&'.$entry.'(@$input)');
			if ($@) {
				print "ERR: $@\n";
			} else {
				if ($out eq $expect) {
					$ok++;
					print "OK\n";
				} else {
					print "$out\n          ERR: expected to be: $expect\n";
				}
			}
		}

		if ($ok == $total) {
			print ">>> Unit clear <<<\n\n";
			$batch_ok++;
		} else {
			print "!!! Unit failure, ".($total-$ok)." cases failed. \n\n";
		}
		$c_total += $total;
		$c_ok += $ok;
	}

	my ($rate_batch, $rate_case)
		= (1000*$batch_ok/$batch_total, 1000*$c_ok/$c_total);
	$rate_batch = int($rate_batch)/10;
	$rate_case  = int($rate_case )/10;

	print <<"EOM";
TEST SUMMARY
===============================================================
  Total units:       $batch_total
  Passed units:      $batch_ok
  Total cases:       $c_total
  Passed cases:      $c_ok
  Unit passed rate:  $rate_batch%
  Case passed rate:  $rate_case%
EOM
	if (wantarray) {
		return $batch_ok == $batch_total;
	} else {
		return ($batch_total, $batch_ok, $c_total, $c_ok);
	}
}


@ISA = qw(Exporter);
@EXPORT = qw(
	test_batch
	);

1;
