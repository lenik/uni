# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl Net-Adapter.t'

#########################

# change 'tests => 1' to 'tests => last_test_to_print';

use Test::More tests => 1;
BEGIN { use_ok('Net::Adapter') };

#########################

# Insert your test code below, the Test::More module is use()ed here so read
# its man page ( perldoc Test::More ) for help writing this test script.
use Data::Dumper;

sub dmp {
    diag "Dump: ".Dumper(shift);
}

my %adaps;
enum_adapters sub {
    my $adap = shift;
    $adaps{$adap->AdapterName} = $adap->Description;
};
diag Dumper(\%adaps);

my $ret;
diag 'Reset vm...';
$ret = netcon_reset('{F3F4AE2D-B300-440E-A6A1-EDF01591CCED}');
diag sprintf '  %x', $ret;

diag 'Reset intel...';
$ret = netcon_reset('{4C7F5537-1402-4E98-9788-AA8B12DC7954}');
diag sprintf '  %x', $ret;
