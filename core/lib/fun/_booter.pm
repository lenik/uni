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
sub _info;
sub _info2;
sub _version;
sub _help;
sub _main;
sub _expand;
sub _load;

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
our $opt_version;
our $opt_help;
our $opt_ver_title      = "Perl simple function caller";
our $opt_ver_id         = '$Id$';
our $opt_funname;
our $opt_funcode;
our $opt_funpkg;
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
        $opt_load_all   = 1;
        $opt_helpmore   = "Examples: \n    fun ~hello world\n";
    } else {
        $opt_funpkg     = 'fun::'.$name;
        _info2 "try load default lib of funpack: $opt_funpkg";
        _load $opt_funpkg;
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
               'version',
               'help|h',
               'load-lib|l=s'   => sub { shift; _load _expand(shift) },
               );

    if (('fun' eq lc $name) or !($opt_version or $opt_help)) {
        die("fun name isn't specified") if $opt_funname eq '';
    }

    _load _expand($_) for @ARGV;

    @ARGV = @FARGV;
    _info2 "fun name: $opt_funname";
    _info2 "fun arg: $_" for @ARGV;

    my $ret = _main;

    _info2 "fun $opt_funname returns: $ret";
    return $ret;
}

sub _info {
    return if $opt_verbose < 1;
    my $text = shift;
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub _info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub _version {
    my %id = parse_id($opt_ver_id);
    print "[$opt_verbtitle] $opt_ver_title\n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";

    #if ($opt_funpkg->can('version')) {
    #    print "\n";
    #    $opt_funpkg->version;
    #}
}

sub _help {
    _version;
    print <<"EOM";

        This is a program based on fun-framework.
        The program is distributed under GPL.

Syntax (fun's common CLI interface):
    $funpack->{path} [<options>] [<load-libs>...]
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

    if ($opt_funpkg->can('help')) {
        print "\n                 -*- Funpack Help -*-\n";
        print "        ---------------------------------------\n";
        $opt_funpkg->help(1);
    }
}

sub _main {
    if ($opt_load_all) {
        for my $d (which_package('fun::')) {
            _info2 "scan for autoload: $d/";
            for ((new DirHandle($d))->read) {
                next unless s/\.pm$//;
                next unless $_ =~ /^\w+$/;
                _load "fun::$_";
            }
        }
    }

    $0 = $opt_funname;

    return _help    if $opt_help;
    return _version if $opt_version;

    die "Unknown fun $opt_funname" unless __PACKAGE__->can($opt_funname);

    $opt_funcode = eval('\&'.$opt_funname);
    $opt_funpkg  = which_sub($opt_funcode) unless defined $opt_funpkg;

    if ($opt_funpkg->can('boot')) {
        _info2 "boot $opt_funpkg";
        $opt_funpkg->boot;
    }
    my $err = $opt_funcode->(@ARGV);
    if ($opt_funpkg->can('exit')) {
        _info2 "exit $opt_funpkg";
        $err = $opt_funpkg->exit;
    }
    return $err;
}

sub _expand {
    my $pkg = shift;
    $pkg = "fun:$pkg" if $pkg =~ m/^:/;
    return $pkg;
}

our %loaded;
sub _load {
    my $pkg = shift;
    unless (defined $loaded{$pkg}) {
        _info2 "loading $pkg";
        $loaded{$pkg} = _use $pkg;
    }
}

1