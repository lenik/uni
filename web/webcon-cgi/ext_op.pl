#!/usr/bin/perl

use strict;
use Config;
use CGI;


print <<"EOB";
Content-type: text/html

<html><body>
	<h1>Login</h1>Login name:
EOB

print getlogin()."<BR>\n";

print "<h1>CGI::Q</h1>\n";
my $query = new CGI;
foreach (keys %$query) {
	my $v = $query->{$_};
	if (ref($v) eq 'ARRAY') {
		my $res = '';
		foreach (@$v) {
			if ($res ne '') { $res .= " <em><font color='#FFAAAA'>and</font></em> "; }
			$res .= $_;
		}
		$v = $res;
	}
	print "$_ = $v <BR>\n";
}

print "<h1>environemnt</h1>\n";
foreach (keys %ENV) {
        print "$_ = $ENV{$_} <BR>\n";
}

print "<h1>\@INC</h1>\n";
foreach (@INC) {
        print "$_ <BR>\n";
}

print "<h1>\%Config</h1>\n";
foreach (keys %Config) {
	print "$_ = $Config{$_} <BR>\n";
}

print <<"EOB";
	</body>
</html>
EOB
