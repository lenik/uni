package WWW::Xget::StarDict;

=head1 NAME

WWW::Xget::StarDict - StarDict Xget driver

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 0;
use cmt::log(2);
use cmt::netutil('loadurl');
use cmt::util('addopts_long', 'ieval');
use cmt::utree;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: StarDict.pm,v 1.1 2007-09-29 11:31:06 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use LWP::Simple('get');
use WWW::Xget;

our @ISA    = qw(WWW::Xget::Driver);
our @EXPORT = qw();

sub mktree;

# INITIALIZORS

=head1 DESCRIPTION

B<WWW::Xget::StarDict> is a Xget driver.

=cut
sub new {
    my $package = shift;
    my $this = {
        'OPTIONS'       => [
            'user|u=s',
            'password|p=s',
        ],
        'prefix'        => 'http://www.stardict.org/',
        'DESCRIPTION'   => 'dict files for stardict',
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
    my $tree = ieval 0, sub { *__ANON__ = '<0>';
        _log1 'building download tree';
        mktree 'download.php'
    };
    my $list = ieval 1, sub { *__ANON__ = '<1>';
        my @list = findhref $tree;
        \@list
    };
    for (@$list) {
        print "downloading ", $_, "\n";
    }
}

sub mktree {
    my $this = shift;
    my $url = $this->{'prefix'}.shift;
    my $tree = {};
    _log1 "  load $url";
    my $t = new cmt::utree;
    $t->parse(loadurl $url);
    for ($t->find('a')) {
        next unless my $onclick = $_->attr('onclick');
        next unless $onclick =~ /(\w+)\('(.*?)'\)/;
        my ($fun, $id) = ($1, $2);
        if ($fun eq 'getSubTree') {
            my $id_user = $id;
            $id_user =~ s/[[:punct:]]/_/g;
            $tree->{$id_user} = mktree 'dirinfo.php?showadddict=0&parent='.$id;
        } elsif ($fun eq 'showDictInfo') {
            my $url_i = $this->{'prefix'}.'dictinfo.php?uid='.$id;
            _log1 '  loadinfo '.$url_i;
            my $info = loadurl $url_i;
            my %info;
            while ($info =~ /<b>([^<]+): <\/b>([^<]+)<br>/g) {
                $info{$1} = $2;
            }
            my $it = new cmt::utree;
            $it->parse($info);
            if (my $downlink = $it->find('a')) {
                # push @all_href,
                $info{'.href'} = $downlink->attr('href');
            }
            $tree->{$id} = \%info;
        }
    }
    $tree
}

sub findhref {
    my $node = shift;
    ($node->{'.href'}) ||
        map { ref $_ ? findhref $_ : () } values %$node
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