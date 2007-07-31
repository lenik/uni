package fun::_booter;

use strict;
use cmt::path;
use cmt::perlsys;
use cmt::util;
use cmt::vcs;
use Data::Dumper;
use DirHandle;
use Exporter;
use Getopt::Long;

sub _boot;
sub _main;
sub _load;
sub _info;
sub _info2;
sub _version;
sub _help;

our @ISA                = qw(Exporter);
our @EXPORT             = qw(_boot);
our %EXPORT_TAGS        = (info => [qw($funpack), @EXPORT],
                           pack => [qw($opt_ver_title
                                       $opt_ver_id
                                       $opt_helpmore
                                       $opt_load_all), @EXPORT],
                           );
our @EXPORT_OK          = map {@$_} values %EXPORT_TAGS;

our $opt_verbtitle      = '?';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_ver_title      = "Perl simple function caller";
our $opt_ver_id         = '$Id: _booter.pm,v 1.1 2007-07-31 14:59:43 lenik Exp $';
our $opt_funname;
our $opt_helpmore;
our $opt_load_all       = 0;

our $funpack = {
    verbtitle           => \$opt_verbtitle,
    verbose             => \$opt_verbose,
    ver_title           => \$opt_ver_title,
    ver_id              => \$opt_ver_id,
    funname             => \$opt_funname,
    load_all            => \$opt_load_all,
};

sub _boot {
    my ($dir, $base)    = path_split($0);
    my ($name, $ext)    = path_splitext($base);
    $funpack->{path}    = $0;
    $funpack->{dir}     = $dir;
    $funpack->{base}    = $base;
    $funpack->{name}    = $name;
    $funpack->{ext}     = $ext;
    if ('fun' eq lc($opt_verbtitle = $name)) {
        $opt_load_all    = 1;
        $opt_helpmore   = "Examples: \n    fun ~hello world\n";
    }

    my @FARGV;
    for my $i (0..$#ARGV) {
        my $arg = $ARGV[$i];
        if ($arg =~ m/^[~\/]/) {
            @FARGV = splice(@ARGV, $i + 1);
            $opt_funname = substr(pop @ARGV, 1);
            last;
        }
    }
    GetOptions('quiet|q'        => sub { $opt_verbose-- },
               'verbose|v'      => sub { $opt_verbose++ },
               'version'        => sub { _version; exit },
               'help|h'         => sub { _help; exit },
               'load-lib|l=s'   => sub { shift; _load shift },
               );

    die("fun name isn't specified") if $opt_funname eq '';
    _load $_ for @ARGV;

    @ARGV = @FARGV;
    $0 = $opt_funname;
    _info2 "fun name: $0";
    _info2 "fun arg: $_" for @ARGV;

    my $ret = _main;

    _info2 "fun $0 returns: $ret";
    return $ret;
}

sub _info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub _info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub _version {
    my %id = parse_id($opt_ver_id);
    print "[$opt_verbtitle] $opt_ver_title\n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub _help {
    _version;
    print <<"EOM";

        This is a program based on fun-framework.
        The program is distributed under GPL.

Syntax (fun's common CLI interface):
    $0 [<options>] [<load-libs>...]
            ~<fun-name> (or /<fun-name>) [<fun-options>...]

Common options:
    -l, --loadlib=PACKAGE   load a lib of perl module
        (PACKAGE: full::qualified or fun::rel as ':rel')
    -h, --help              show this help page
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
        --version           print the version info
EOM
    print "\n".$opt_helpmore if $opt_helpmore ne '';
}

sub _main {
    my $sub = $0;
    my $err;
    # unless necessary libraries are used.
    unless (main->can($sub)) {
        if ($opt_load_all) {
            for my $d (which_package('fun::')) {
                _info2 "scan for autoload: $d/";
                for ((new DirHandle($d))->read) {
                    next unless s/\.pm$//;
                    next unless $_ =~ /^\w+$/;
                    _load ":$_";
                }
            }
        } else {
            _info2 "try load default lib of funpack: fun::$funpack->{name}";
            _load ':'.$funpack->{name};
        }
        die "Unknown fun $sub" unless __PACKAGE__->can($sub);
    }

    $sub = eval('\&'.$sub);
    my $pkg = which_sub($sub);
    if ($pkg->can('boot')) {
        _info2 "boot $pkg";
        $pkg->boot;
    }
    $err = $sub->(@ARGV);
    if ($pkg->can('exit')) {
        _info2 "exit $pkg";
        $err = $pkg->exit;
    }
    $err
}

our %loaded;
sub _load {
    my $pkg = shift;
    $pkg = "fun:$pkg" if $pkg =~ m/^:/;
    unless (defined $loaded{$pkg}) {
        _info2 "loading $pkg";
        $loaded{$pkg} = _use $pkg;
    }
}

1