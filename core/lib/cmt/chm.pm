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
             chm_compile
             );

sub parse_attributes {
    my $s = shift || $_;
    while (/(\w+)\s*=\s*(".*?"|'.*?'|\S+)/g) {
        my ($k, $v) = ($1, $2);
        $v = $2 if $v =~ /^(["'])(.*?)\1$/;
        push @_, [$k, $v];
    }
    return @_;
}

sub htmlinfo {
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
                for (parse_attributes) {
                    my ($k, $v) = (lc $_->[0], $_->[1]);
                    if ($k eq 'name') {
                        $nam = $v;
                    } elsif ($k eq 'content') {
                        $cnt = $v;
                    }
                    push @{$info->{lc $nam}}, $cnt if defined $nam and defined $cnt;
                }
            } elsif ($tag eq 'a') {
                for (parse_attributes) {
                    if (lc $_->[0] eq 'name') {
                        push @{$info->{'.a'}}, $_->[1];
                    }
                }
            }
        }
    } -qset => q(<), -rem => 1, @_;
    return $info;
}

sub xml_value {
    my $v = shift || $_;
    return $v;
}

sub sitemap {
    my ($name, $loc) = @_;
    return '<LI><OBJECT type="text/sitemap"><param name="Name" value="'
            . xml_value($name) . '"><param name="Local" value="'
            . $loc . '"></OBJECT></LI>'."\n";
}

sub chm_hhp {
    my %cfg         = @_;
    my $files       = $cfg{-files};
    $cfg{-output}   ||= 'a.chm';
    $cfg{-tocfile}  ||= 'toc.hhc';
    $cfg{-idxfile}  ||= 'index.hhk';
    $cfg{-default}  ||= $files->[0];
    $cfg{-title}    ||= 'cmt::chm example';
    $cfg{-lang}     ||= '0x0804 Chinese';

    unindent <<"EOM" . join("\n", @$files);
    [OPTIONS]
    Binary TOC=No
    Compatibility=1.1 or later
    Compiled file=$cfg{-output}
    Contents file=$cfg{-tocfile}
    Index file=$cfg{-idxfile}
    Default Window=Main
    Default topic=$cfg{-default}
    Display compile progress=No
    Full-text search=Yes
    Language=$cfg{-lang}
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
        sitemap($title, $file) . (@child
            ? "<UL>\n".join("\n", map { hhc_node($_) } @child)."</UL>\n"
            : '');
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
    </BODY>
    </HTML>

EOM
    return $buf;
}

sub chm_hhk {
    my %cfg = @_;
    my $idx = $cfg{-index};
    my $buf = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML//EN\">\n<HTML>\n<BODY>\n";
    for (sort keys %$idx) {
        $buf .= sitemap($_, $idx->{$_});
    }
    $buf .= "</BODY>\n</HTML>\n";
    return $buf;
}

sub chm_build {
    my %cfg     = @_;
    my $basedir = $cfg{-basedir} || '.';
    my $prjfile = $cfg{-prjfile};
    my $preview = $cfg{-preview};
    unless (defined $prjfile) {
        $prjfile = 'a.hhp';
        if ($cfg{-output}) {
            my ($dir, $base) = path_split($cfg{-output});
            my ($file, $ext) = path_splitext($base);
            $prjfile = $file.'.hhp';
        }
        $cfg{-prjfile} = $prjfile;
    }
    my $tocfile = $cfg{-tocfile};
    my $idxfile = $cfg{-idxfile};

    writefile path_join($basedir, $prjfile), chm_hhp(%cfg);
    writefile path_join($basedir, $tocfile), chm_hhc(%cfg);
    writefile path_join($basedir, $idxfile), chm_hhk(%cfg);

    return 1 if $preview;

    my $ret = chm_compile(%cfg);

    unlink $prjfile;
    unlink $tocfile;
    unlink $idxfile;

    return $ret;
}

sub chm_compile {
    my %cfg     = @_;

    my $prjfile = $cfg{-prjfile};
    my $tocfile = $cfg{-tocfile};
    my $idxfile = $cfg{-idxfile};

    open (CAP, "hhc $prjfile|")
        or die "can't invoke hhc utility to do the compilation: $!";
    while (<CAP>) {
        print "hhc> $_";
    }
    close CAP;
    return 1;
}

1

__END__

%htmlinfo:
    .title          TITLE
    .a              [ anchors... ]
    meta-name       meta-value

%chm-config structure:
    -output         a.chm
    -tocfile        toc.hhc
    -idxfile        index.hhk
    -default        ? ( files[0] )
    -title          cmt::chm example
    -lang           0x0804 Chinese
    -files          [ files, ... ]
    -roots          [ node, node, ... ]
                    node: [ file, title, node, node, ... ]
    -index          { keyword => file }
    -basedir        where write .hhc and .hhk to
    -preview        no make and clean
