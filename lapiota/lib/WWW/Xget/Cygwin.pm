package WWW::Xget::Cygwin;

=head1 NAME

WWW::Xget::Cygwin - Cygwin Xget driver

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 0;
use cmt::log(2);
use cmt::util('addopts_long');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use WWW::Xget;

our @ISA    = qw(WWW::Xget::Driver);
our @EXPORT = qw();

# INITIALIZORS
my  %WKFILES =          # well known files
    qw(
        setup.hint      1
        md5.sum         1
    );

=head1 DESCRIPTION

B<Cygwin> is a Xget driver.

=cut
sub new {
    my $package = shift;
    my $this = {
        'OPTIONS'       => [
            'user|u=s',
            'password|p=s',
            'prefix=s',
            'test',
        ],
        'prefix'        => 'http://www.cygwin.cn/pub',
        'DESCRIPTION'   => 'for cygwin mirrors',
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
    # Parse setup.ini
    my %pkgs;
    my $pkgname = '__setup__';
    my $sect;
    my $attr = {};
    my $file = join('', <>);
    _log "Preprocess...";
    my @mem = _qeval($file, undef, '"');
    _log "Parsing...";
    $| = 1;
    while ($file =~ /^(?:\@\s*((?:\w|-)+) | \[(.*)\] | ((?:\w|-)+):\s*(.*))$/xmg) {
        #                 1         2        3             4
        if (substr($&, 0, 1) eq '@') {
            print "$1          \r";
            $pkgs{$pkgname} = $attr;
            $pkgname = $1;
            $sect = undef;
            $attr = {};
        } elsif (substr($&, 0, 1) eq '[') {
            # _log "section $2";
            $sect = $2;
        } elsif (!defined $sect) {
            my ($k, $v) = ($3, $4);
            $attr->{$k} = $v;
        }
    }
    _log "Parsed";

    # _log "Get download list";

    _log "Scan local files";
    my %local;
    #for (listdir_rec '.') {
    #    $local{$_} = 1;
    #}

    my %got;
    sub wantfile {
        my $file = shift;
        if (exists $got{$file}) {
            # already got.
        } elsif (exists $local{$file}) {
            _log "  got $file";
            $got{$file} = 1;
            delete $local{$file};
        } else {
            _log "  lost $file";
        }
    }

    _log "Remove unreferenced packages. ";
    _log '(You should run this program under the directory contains release/)';
    for my $pkg (sort keys %pkgs) {
        _log "package $pkg";
        my $attr = $pkgs{$pkg};
        my ($insfile) = parse_ipath $attr->{'install'};
        my ($srcfile) = parse_ipath $attr->{'source'};
        wantfile $insfile;
        wantfile $srcfile;
    }

    my @unlink;
    for (keys %local) {
        my ($dir, $file) = path_split($_);
        my $t = lc $file;
        next if defined $WKFILES{$t};
        push @unlink, $_;
    }

    for (@unlink) {
        print "unlink $_\n";
        unlink $_ ;#unless $opt_test;
    }
}

sub listdir_rec {
    my $start = shift;
    my $noprefix = "$start/";
    my $noprelen = length $noprefix;
    my @list;
    my $count = fswalk {
        my $path = shift;
        if (-d $path) {
            print "$path/          \r";
            return 1;
        }
        # $path = substr($path, $noprelen) if substr($path, 0, $noprelen) eq $noprefix;
        # _log $path;
        push @list, $path;
        return 1;
    }, -start => $start;
    @list
}

sub parse_ipath {
    # install: release/ELFIO/ELFIO-1.0.2-1.tar.bz2 159970 41b32111cbaaad78ea56dc62b757dd46
    my ($path, $size, $hash) = split(/\s+/, shift);
    # my ($_release, $pkg, $rel) = split(/[\/]/, $path, 3);
    # my ($rel_dir, $rel_file) = path_split($rel);
    # my $fdir = path_join($_release, $pkg, $rel_dir);
    return ($path); # wantarray ? ($fdir, $rel_dir, $rel_file) : $path;
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