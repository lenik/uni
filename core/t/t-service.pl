
use strict;
use cmt::service;
use Data::Dumper;

my $host = undef;
my $name = 'helpsvc';

my $ret = service_stop($name);
print "stop ret = $ret\n";
print "[$@] $!\n";

$ret = service_start($name);
print "start ret = $ret\n";
print "[$@] $!\n";

__END__
my $list = service_list;
for my $name (keys %$list) {
    my $disp = $list->{$name};
    print "\nService $disp ($name)\n";

    my $status = service_status($name);
    my $info = Dumper($status);
    $info =~ s/^.*=\s*/        /;
    print $info;
}
