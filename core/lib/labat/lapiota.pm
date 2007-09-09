package labat::lapiota; # package description

use strict;
use Getopt::Long;
use Exporter;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(getopts
                 findabc
                 );

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

1