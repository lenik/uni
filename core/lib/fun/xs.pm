package fun::xs;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::pp;
use cmt::util;
use cmt::vcs;
use Data::Dumper;
use Exporter;
use Getopt::Long;
use YAML;

@ISA    = qw(Exporter);
@EXPORT = qw(
             stwrap
             );

sub boot;
sub info;
sub info2;
sub version;
sub help;
sub stwrap;
sub stdump;
sub addtype;
sub typedump;

our $opt_verbtitle      = 'xsutil';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_package;
our $opt_pkgprefix;
our $opt_before;
our $opt_after;

my $TYPES;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               'package|p=s',
               'before|b=s',
               'after|a=s',
               );
    if (defined $opt_package) {
        $opt_pkgprefix = $opt_package;
        $opt_pkgprefix =~ s/::/_/g;
        $opt_pkgprefix .= '_';
    }
    info2 "before:  $opt_before";
    info2 "after:   $opt_after";
    print readfile($opt_before) if defined $opt_before;
}

sub exit {
    print readfile($opt_after) if defined $opt_after;
}

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub version {
    my %id = parse_id('$Id: xs.pm,v 1.3 2007-07-13 11:42:50 lenik Exp $');
    print "[$opt_verbtitle] XSUB Utilities \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        fun ~command <options>...

Common Options:
        --package=<perl-package> (p, eg. "MyPkg::MySubPkg")
        --before=<file> (b, output before the body)
        --after=<file> (a, output after the body)
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)

Commands:
        ~stwrap     C-Struct Wrapper
EOM
}

sub stwrap {
    my $name;
    my %mm;

    ppcmt {
        # struct (XXX)
        if (/struct \s+ (\w*) (?!.*;)/x) {
            stdump $name, \%mm if defined $name;
            $name = $1;
            undef %mm;
            next;
        }

        # typedef... } (*XXX (, *XXX)) ;
        if (/\}\s*(\*?\w+(?:\s*,\s*\*?\w+)*)\s*;/) {
            my @typedefs = split(/\s*,\s*/, $1);
            for (@typedefs) {
                stdump $_, \%mm;
            }
            undef $name;
            undef %mm;
            next;
        }

        # (long long int *) (MEMBER (, MEMBER2)) ([10][20]);
        my $_var = qr/(\w+)((?:\s*\[\s*\w*\s*\])*)/;
        while (/^\s* (.+?) \s+ ($_var(?:,\s*$_var)*) \s*;/xg) {
            my $type = $1;
            my @mems = split(/\s*,\s*/, $2);
            # info "MEMBER ".join(' - ', @mems).": $type";
            for (@mems) {
                die "Unexpected: $_ should match $_var as it just did"
                    unless /^$_var$/;
                my $nam = $1; $_ = $2;
                my @dim;
                push @dim, $1 while /\[\s*(\w*)\s*\]/g;
                $mm{$nam} = addtype($type, @dim);
                push @{$mm{'<'}}, $nam;
            }
        }
    };
    stdump $name, \%mm if defined $name;
    typedump;
}

sub INDENTED {
    my @lines = split(/\n/, shift);
    my ($s) = ($lines[0] =~ /^(\s*)/);
    my $l = length $s;
    join("\n", map { substr($_, $l) } @lines);
}

sub stdump {
    my ($n, $mm) = @_;
    info2 "dump: $n => ".Dumper($mm);

    # (lib_stname_)member
    my $prefix = $opt_pkgprefix.$n.'_';
    for (@{$mm->{'<'}}) {
        my ($nam, $type) = ($_, $mm->{$_});
        my $chunk = INDENTED<<"EOM";
            $type
            ${prefix}$nam(self)
                    struct $n *self;
                CODE:
                    RETVAL = self->$nam;
                OUTPUT:
                    RETVAL

            void
            ${prefix}set$nam(self, $type newval)
                    struct $n *self;
                CODE:
                    self->$nam = newval;
                OUTPUT:
                    self


EOM
        print $chunk;
    }
}

sub addtype {
    my $type = shift; # @_ == @dim
    # struct|class TYPE **
    $type =~ s/\s+/_/g;
    my $orig = [$type, @_];

    $type =~ s/\W/_/g;

    for (@_) {
        s/\s+/_/g;
        s/\W/_/g;
        $type .= '_'.$_;
    }
    # $type .= '_t';

    if (defined $TYPES->{$type}) {
        # warn "ambiguous type: $type" if ne;
    } else {
        $TYPES->{$type} = $orig;
    }
    return $type;
}

sub typedump {
    # info2 Dumper($TYPES);
    for my $type (keys %$TYPES) {
        my $info = $TYPES->{$type};
        my $tname = $info->[0];
        my $typedef = "typedef $tname $type";
        if ($#$info) { # have dims
            # typedef $tname $type [$dim]...;
            $typedef .= ' ';
            $typedef .= "[$_]" for @$info[1..$#$info];
        }
    }
}

1