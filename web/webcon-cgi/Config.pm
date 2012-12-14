
package webcon::Config;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;
use vars qw/%WConf/;


%WConf = (
	'debug'					=> 1,
	'listdir_overload'		=> '.webcon.view.cgi',
	'default_mime'			=> 'application/octet-stream',
	);

sub isDebug {
	$WConf{'debug'};
}


@ISA = qw(Exporter);
@EXPORT = qw(
	%WConf
	isDebug
	);


1;
