package WWW::Xget::SourceForge;

=head1 NAME

WWW::Xget::SourceForge - SourceForge Xget driver

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 0;
use cmt::log(2);
use cmt::util('addopts_long');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: SourceForge.pm,v 1.2 2007-09-29 11:31:06 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use WWW::Xget;

our @ISA    = qw(WWW::Xget::Driver);
our @EXPORT = qw();

# INITIALIZORS

=head1 DESCRIPTION

B<WWW::Xget::SourceForge> is a Xget driver.

=cut
sub new {
    my $package = shift;
    my $this = {
        'DESCRIPTION'   => 'sf.net releases',
        'HELP'          => help(),
        'VERSION'       => $VER,
        'packages'      => [],
        'patterns'      => [],
    };
    $this->{'OPTIONS'} = [
        'source|s',
        'user|u=s',
        'password|p=s',
        'package|k=s' => $this->{'packages'},
        'pattern|r=s' => $this->{'patterns'},
        'all-packages|a',
        'full-package|f',
    ];
    addopts_long $this->{'OPTIONS'}, $this;
    bless $this, $package;
    $this
}

sub help { << 'EOM';
    -s, --source            checkout source code through VCS
    -u, --user=USER         login with USER
    -p, --password=PASSWORD login with PASSWORD
    -k, --package=PACKAGE   include this PACKAGE
    -r, --pattern=REGEXP    get files match REGEXP, instead of the most recent
    -a, --all-packages      get all packages
    -f, --full-package      get all files under a package
EOM
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1