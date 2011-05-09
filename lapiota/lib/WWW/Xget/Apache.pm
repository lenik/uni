package WWW::Xget::Apache;

=head1 NAME

WWW::Xget::Apache - Apache Xget driver

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 0;
use cmt::log(2);
use cmt::netutil('loadurl');
use cmt::util('addopts_long', 'ieval');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use Parse::Apache::Index;
use WWW::Xget;

our @ISA    = qw(WWW::Xget::Driver);
our @EXPORT = qw();

# INITIALIZORS

=head1 DESCRIPTION

B<WWW::Xget::Apache> is a Xget driver.

=cut
sub new {
    my $package = shift;
    my $this = {
        'OPTIONS'       => [
            'user|u=s',
            'password|p=s',
        ],
        'start_dyn'     => 'http://www.apache.org/dyn/closer.cgi',
        'DESCRIPTION'   => 'Apache.org released softwares',
        'HELP'          => help(),
        'VERSION'       => $VER,
    };
    addopts_long $this->{'OPTIONS'}, $this;
    bless $this, $package;
    $this
}

sub help {<< 'EOM';
    -u, --user=USER         login with USER
    -p, --password=PASSWORD login with PASSWORD
EOM
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)
sub _main {
    die "Base name doesn't specified" unless @ARGV;
    my $base = shift @ARGV;

    # http://www.apache.org/dyn/closer.cgi
    my $opt_start_dyn = undef;
    _sig1 'dyn', $opt_start_dyn;
    my $dyn = loadurl $opt_start_dyn;
    unless ($dyn =~ /<a name="Welcome">.*?<a href="([^"]+)"/i)
        { die "failed to get mirror: can't parse dyn: $opt_start_dyn" }

    # http://apache.mirror.phpchina.com/
    my $mirror = $1;
    $mirror .= '/' unless $mirror =~ m-/$-;
    _sig1 'mirrow', $mirror;

    # http://apache.mirror.phpchina.com/commons/ant
    my $start = $mirror.$base;
    _sig1 'start', $start;

    download $start, '.';
}

sub download {
    my ($remote, $local, $prefix) = @_;
    for (listdir_http $remote) {
        _sig1 'down', $prefix.$_;
        my $down = loadurl $remote.'/'.$_;
    }
}


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