package WWW::Xget::SourceForge;

=head1 NAME

WWW::Xget::SourceForge - SourceForge Xget driver

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::log(2);
use cmt::util('addopts_long', 'ieval');
use cmt::utree;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: SourceForge.pm,v 1.3 2007-09-30 10:20:52 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use LWP::Simple('get');
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
        'filep'         => [],
        'archp'         => [],
        'typep'         => [],
    };
    $this->{'OPTIONS'} = [
        'source|s',
        'user|u=s',
        'password|p=s',
        'package|k=s' => $this->{'packages'},
        'all-packages|a',
        'full-package|f',
        'file-pattern|rf=s' => $this->{'filep'},
        'arch-pattern|ra=s' => $this->{'archp'},
        'type-pattern|rt=s' => $this->{'typep'},
    ];
    addopts_long $this->{'OPTIONS'}, $this;
    bless $this, $package;
    $this
}

sub help { << 'EOM';
    -s, --source            checkout source code through VCS
    -u, --user=USER         login with USER
    -p, --password=PASSWORD login with PASSWORD

    (default to get the first project)
    -k, --package=PACKAGE   include this PACKAGE
    -a, --all-packages      get all packages

    (default to get the first/most-recent file)
    -f, --full-package          get all files under a package
    -rf,--file-pattern=REGEXP   select by matching filename against REGEXP
    -ra,--arch-pattern=REGEXP   select by matching archname against REGEXP
    -rt,--type-pattern=REGEXP   select by matching typename against REGEXP

EOM
}

sub do_get {
    my $this = shift;
    my $count = 0;
    for (@_) {
        _sig1 $_, 'get group-id';
        my $id = ieval($_.'.1', sub { *__ANON__ = '<1>';
            my $t = get 'http://sourceforge.net/projects/'.$_;
            $t =~ m|<a href=\"(/project/showfiles\.php\?group_id=(\d+))#downloads|
                or return undef;
            return $2;
        }) or die "invalid project name: $_";
        _sig1 $_, 'list download files';
        my $files = ieval($_.'.2', sub { *__ANON__ = '<2>';
            my $t = get 'http://sourceforge.net/project/showfiles.php?group_id='.$id;
            $t = new cmt::utree->parse($t);
            return [];
        }, 30);
        _sig1 $_, 'filter files to get';
        for (@$files) {
        }
        unless (@$files) { _log1 "$_: no files to get"; next }
        for (@$files) {
            _log1 "get...";
            $count++;
        }
    }
    return $count;
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