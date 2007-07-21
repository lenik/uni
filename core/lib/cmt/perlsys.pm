package cmt::perlsys;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::path;
use B;
use Exporter;
use YAML;

sub perlsh {
    my @ret;
    my $buf;
    local ($|) = 1;

    while (<STDIN>) {
        chomp;

        # ctrl-d
        last if $_ eq chr(4);

        if (!/;\s*$/ and /^\s|\s$/) {
            $buf .= "\n".$_;
            next;
        }
        if (defined $buf) {
            $_ = $buf . $_;
            undef $buf;
        }
        undef $!;
        @ret = eval('package main; no strict; '.$_);
        if ($@) {
            print STDERR "ERR $@ - $!\n" if $@;
        } else {
            print Dump(@ret) if @ret;
        }
    }
    return wantarray ? @ret : $ret[0];
}

sub which_package {
    my $pkg = shift;
    (my $relpath = $pkg) =~ s/::/\//g;
    my ($reldir, $base) = path_split $relpath;
    my $parent_only = $base eq '';
    my @found;
    for (@INC) {
        my $dir = path_join($_, $reldir);
        if (-d $dir) {
            if ($parent_only) {
                push @found, $dir;
            } else {
                my $file = path_join($dir, "$base.pm");
                if (-f $file) {
                    push @found, $file;
                }
            }
        }
    }
    return wantarray ? @found : $found[0];
}

sub which_sub {
    my $sub = shift;
    my $cv = B::svref_2object($sub);
    if (wantarray) {
        return ($cv->STASH->NAME(), $cv->FILE(), $cv->GV->LINE());
    } else {
        return $cv->STASH->NAME();
    }
}

@ISA    = qw(Exporter);
@EXPORT = qw(perlsh
	     which_package
	     which_sub);

1;
