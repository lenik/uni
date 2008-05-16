package labat::lapiota;

=head1 NAME

labat::lapiota - Lapiota System Functions

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = $labat::LOGLEVEL;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use labat;
use Exporter;
use Getopt::Long;

our @ISA    = qw(Exporter);
our @EXPORT = qw(getopts
                 findabc
                 findexist
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use lapiota;
    mysub(arguments...)

=head1 Lapiota System Functions

B<lapiota> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-lapiota-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub getopts(\@@) {
    my @bak = @ARGV;
    my $a = shift;
    @ARGV = @$a;
    my $result = GetOptions(@_);
    @$a = @ARGV;
    @ARGV = @bak;
    $result
}

sub findabc {
    my $ctx = shift;
    my ($root, $print, $style) = ($ENV{'LAPIOTA'}.'/abc.d', 0, 'm');
    getopts(@_,
        'root|r=s'  => \$root,
        'print|p'   => \$print,
        'unix|u'    => sub { $style = 'u' },
        'windows|w' => sub { $style = 'w' },
        'mix|m'     => sub { $style = 'm' },
        );
    my ($name, @addpath) = @_;
    my $chdir = $name =~ s/\/$//;
    my $lev = 0;
    my $prefix = $root;
        $prefix =~ s/\\/\//g;
    my $home;
    while ($lev <= length($name)) {
        if (my @glob = <$prefix/$name*>) {
            $home = $glob[0];
            last
        }
        $prefix .= '/'.substr($name, 0, ++$lev)
    }
    return undef unless defined $home;
    my $DFS = $style eq 'w' ? '\\' : '/';
    $home =~ s/\//$DFS/g;
    $ENV{'_HOME'} = $home;
    chdir $home if $chdir;
    print "$home\n" if $print;
    my $IFS = $style eq 'u' ? ':' : ';';
    for (@addpath) {
        my $t = $_ eq '.' ? $home : $home.$DFS.$_;
        $ENV{'PATH'} .= $IFS.$t;
    }
    return $home;
}

sub findexist {
    my $ctx = shift;
    my $f   = sub { -e shift };
    getopts(@_,
        'directory' => sub { $f = sub { -d shift } },
        'file'      => sub { $f = sub { -f shift } },
        );
    my $last = '<empty-arg-list>';
    my @args = map { labat::_resolv($ctx, $_) } @_;
    for (@args) {
        last if $f->($last = $_);
    }
    return $last;
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