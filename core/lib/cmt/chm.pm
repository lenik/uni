package cmt::chm;

use strict;
use vars                qw(@ISA @EXPORT);
use cmt::pp;
use cmt::util;
use Data::Dumper;
use Exporter;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

@ISA    = qw(Exporter);
@EXPORT = qw(htmlinfo
             chm_hhp
             chm_hhc
             chm_hhk
             chm_build
             );

sub htmlinfo {

    sub attrs {
        my $s = shift || $_;
        while (/(\w+)\s*=\s*(".*?"|'.*?'|\S+)/g) {
            my ($k, $v) = ($1, $2);
            $v = $2 if $v =~ /^(["'])(.*?)\1$/;
            push @_, [$k, $v];
        }
        return @_;
    }

    my $info    = {};
    my $tag;
    my $tagbuf;
    pp {
        my $X = shift;
        $tagbuf .= $_ if !$X && defined $tag;
        return unless $X eq '<';
        if (/^\/(\w+)/) {
            if ($tag eq lc $1) {
                if ($tag eq 'title') {
                    $info->{'.title'} = $tagbuf;
                }
            } else {
                # ignore unmatched starttag/endtag
            }
            undef $tag;
            undef $tagbuf;
        } elsif (s/^(\w+)//) {
            $tag = lc $1;
            $tagbuf = '';
            if ($tag eq 'meta') {
                my ($nam, $cnt);
                for (attrs) {
                    my ($k, $v) = (lc $_->[0], $_->[1]);
                    if ($k eq 'name') {
                        $nam = $v;
                    } elsif ($k eq 'content') {
                        $cnt = $v;
                    }
                    push @{$info->{lc $nam}}, $cnt if defined $nam and defined $cnt;
                }
            } elsif ($tag eq 'a') {
                for (attrs) {
                    if (lc $_->[0] eq 'name') {
                        push @{$info->{'.a'}}, $_->[1];
                    }
                }
            }
        }
    } -qset => q(<), -rem => 1, @_;
    return $info;
}

sub chm_hhp {
    my %cfg         = @_;
    my $files       = $cfg{-files};
    $cfg{-output}   ||= 'a.chm';
    $cfg{-tocfile}  ||= 'toc.hhc';
    $cfg{-default}  ||= $files->[0];
    $cfg{-title}    ||= 'cmt::chm example';

    unindent <<"EOM" . join("\n", @$files);
    [OPTIONS]
    Binary TOC=No
    Compatibility=1.1 or later
    Compiled file=$cfg{-output}
    Contents file=$cfg{-tocfile}
    Default Window=Main
    Default topic=$cfg{-default}
    Display compile progress=No
    Full-text search=Yes
    Language=0x0804 Chinese
    Title=$cfg{-title}
    ; Enhanced decompilation=No

    [WINDOWS]
    Main="$cfg{-title}", "$cfg{-tocfile}",,"$cfg{-default}","$cfg{-default}",,,,,0x2520,,0x603006,,,,,,,,0

    [FILES]

EOM
}

sub chm_hhc {

    # node: [ file, title, [ child ], [ child ], ... ]
    sub hhc_node {
        my $node = shift;
        my ($file, $title, @child) = @$node;
        my $buf  = unindent <<"EOM";
        <LI><OBJECT type="text/sitemap">
        <param name="Name" value="$title">
        <param name="Local" value="$file"></OBJECT></LI>

EOM
           $buf .= "<UL>\n".join("\n", map { hhc_node($_) } @child)."</UL>\n" if @child;
        return $buf;
    }

    my %cfg = @_;
    my $roots = $cfg{-roots};
    my $buf = unindent <<'EOM';
    <HTML>
    <HEAD><META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></HEAD>
    <BODY>
    <OBJECT type="text/site properties">
    <param value="Folder" name="ImageType">
    </OBJECT>
    <UL>

EOM
    $buf .= join("\n", map { hhc_node($_) } @$roots);
    $buf .= unindent <<'EOM';
    </UL>
    </BODY></HTML>

EOM
    return $buf;
}

sub chm_hhk {
    my %cfg = @_;
    return '';
}

sub chm_build {
    my %cfg = @_;

}

1