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
             typemap
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
our %opt_select;
our $opt_readonly       = 0;

my $TYPES;              # $typename -> [$orig_typename, $stype, @dim_spec]
my @STYPES;             # ( [\%levels, \%mm]* )
my %WHICHST;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               'package|p=s',
               'before|b=s',
               'after|a=s',
               'select=s'   => sub { $opt_select{$_[1]} = 1 },
               'readonly',
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
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub version {
    my %id = parse_id('$Id: xs.pm,v 1.8 2007-11-08 10:52:38 lenik Exp $');
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
                    --select=<name> (output only select structs)
                    --readonly
EOM
}

sub stwrap {
    my $name;
    my $levels = [[], []];
    my %mm;
    my $st = [ $levels, \%mm ];

    ppcmt {
      # struct: dump and create
        # struct (XXX)
        if (/struct \s+ (\w*) (?!.*;)/x) {
            push @STYPES, $st;
            stdump $name, \%mm if defined $name;
            if ($1 ne '') {
                $name   = "struct $1 *";
                $levels = [["struct $1"], ["struct $1 *"]];
            } else {
                undef $name;
                $levels = [[], []];
            }
            %mm = ();
            $st = [ $levels, \%mm ];
            return;
        }

      # typedef: dump and clean
        # typedef... } (*XXX (, *XXX)) ;
        if (/\}\s*(\**\w+(?:\s*,\s*\**\w+)*)\s*;/) {
            my @typedefs = map { s/\s+//g; $_ } split(/\s*,\s*/, $1);
            for (@typedefs) {
                /^(\**)(\w+)$/;
                my $level = length $1;
                # unshift so prefer over "struct XXX *"
                unshift @{$levels->[$level]}, $2;
            }
            push @STYPES, $st;

            # prefer to dump "LPXXX" than "XXX *"
            if (@{$levels->[1]}) {
                stdump $_, \%mm for @{$levels->[1]};
            } elsif (@{$levels->[0]}) {
                stdump "$_ *", \%mm for @{$levels->[0]};
            } else {
                # ignore this
            }
            undef $name;
            $levels = [[], []];
            %mm = ();
            $st = [ $levels, \%mm ];
            return;
        }

        # (NAME) ([][1+2])
        my $_var = qr/(\w+)((?:\s*\[[^\[\]]*\])*)/;

      # add members
        # (long long int *) (MEMBER (, MEMBER2));
        while (/^\s* (.+?) \b ($_var(?:,\s*$_var)*) \s*;/xg) {
            my $type = $1;
            my @mems = split(/\s*,\s*/, $2);
            # info "MEMBER ".join(' - ', @mems).": $type";
            for (@mems) {
                die "Unexpected: $_ should match $_var as it just did"
                    unless /^$_var$/;
                my $nam = $1; $_ = $2;
                my @dim;
                push @dim, $1 while /\[([^\[\]]*)\]/g;
                $mm{$nam} = addtype($st, $type, @dim);
                push @{$mm{'<'}}, $nam;
            }
        }
    };
    push @STYPES, $st;
    stdump $name, \%mm if defined $name;
    typedump;
}

sub stdump {
    my ($typename, $mm) = @_;
    return if (defined %opt_select and ! defined $opt_select{$typename});

    # (lib_stname_)member
    my $prefix = $opt_pkgprefix.$typename.'_';
    for (@{$mm->{'<'}}) {
        my ($nam, $type) = ($_, $mm->{$_});
        my $origtype    = $TYPES->{$type}->[0];
        my $st          = $TYPES->{$type}->[1];
        my $is_array    = defined $TYPES->{$type}->[2];
        my $is_string   = $type =~ /^char(\b|_)/i;
        if (defined $st) {
            my $l = getlevel($origtype, $st->[0]);
            if ($l >= 0) {
                my $level_prefer = $st->[0]->[$l]->[0];
                if (defined $level_prefer) {
                    $type = $level_prefer;
                }
            }
        }
        my $chunk = join("\n",
            "$type",
            "$nam(self)",
            "        $typename self",
            "    PROTOTYPE: \$",
            "    CODE: ",
            $is_array
                ? ($is_string
                        ? "        strncpy((char *)RETVAL, (const char *)self->$nam, sizeof($type)); "
                        : "        memcpy(RETVAL, self->$nam, sizeof($type)); ")
                : "        RETVAL = self->$nam; ",
            "    OUTPUT: ",
            "        RETVAL",
            $opt_readonly
                ? ()
                : ("",
                    "void",
                    "set_$nam(self, $type newval)",
                    "        $typename self",
                    "    PROTOTYPE: \$\$",
                    "    CODE: ",
                    $is_array
                        ? ($is_string
                                ? "        strncpy((char *)self->$nam, (const char *)newval, sizeof($type)); "
                                : "        memcpy(self->$nam, newval, sizeof($type)); ")
                        : "        self->$nam = newval; ",
                    "    OUTPUT: ",
                    "        self",
                ),
            "\n",
            );
        print $chunk;
    }
}

sub getlevel {
    my ($typename, $levels) = @_;
    my $incl = 0;
    if ($typename =~ s/((\*|\s)+)$//) {   # remove trailing *
        my $ast = $1;
        $ast =~ s/\s//g;
        $incl = length $ast;
    }
    $typename =~ s/(^\s+)|(\s+$)//g;    # trim
    $typename =~ s/\s\s+/ /g;           # normalize space
    for my $l (0..$#$levels) {
        my $level = $levels->[$l];
        next unless defined $level;
        for (@$level) {
            return $l + $incl if $_ eq $typename;
        }
    }
    return -1;
}


sub whichst {
    my ($type, $st) = @_;   # holding the stype under parsing
    return $WHICHST{$type} if exists $WHICHST{$type};
    my $l = getlevel($type, $st->[0]);
    if ($l < 0) {
        undef $st;
        for (@STYPES) {
            $l = getlevel($type, $_->[0]);
            if ($l >= 0) {
                $st = $_;
                last;
            }
        }
    }
    return wantarray ? ($st, $l) : $st;
}

sub addtype {
    my $st      = shift;    # holding the stype under parsing
    my $type    = shift;    # @_ == @dim
    # struct|class TYPE **
    $type       =~ s/(^\s+)|(\s+$)//g;
    $type       =~ s/\s+/ /g;

    $st         = whichst($type, $st);
    my $orig    = [$type, $st, @_];

    $type       =~ s/ /_/g;
    $type       =~ s/\W/_/g;
    for (@_) {
        s/\s+//g;   # space in dim-spec is removed
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

# generate Module.typemap.xsh XS-include file
sub typemap {
    my %SECTION;
    ppcmt {
        # MODULE=XXX::YYY   PACKAGE=XXX::YYY    PREFIX=XXX
        if (/^MODULE\s*=\s*(\S+)/) {
            while (/(\w+)\s*=\s*(\S+)/g) {
                $SECTION{$1} = $2;
            }
            next;
        }

        # TYPEMAP(type)
        if (/TYPEMAP\s*\(\s*(\w+)\s*\)/) {
            my $type = $1;
            print <<"EOM";
$type
_typemap_${type}_output(var)
        UV var
    PROTOTYPE: \$
    CODE:
        RETVAL = ($type) var;
    OUTPUT:
        RETVAL

EOM
        }
    }
}

1