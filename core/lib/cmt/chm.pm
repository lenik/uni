package cmt::chm;

=head1 DESCRIPTION

=head2 Structure of %chm-config:

    -output         a.chm
    -tocfile        toc.hhc
    -idxfile        index.hhk
    -default        ?
    -title          cmt::chm example
    -lang           0x0804 Chinese
    -files          [ files, ... ]
    -roots          [ file, title, attrs, node, node, ... ]
    -index          { keyword => file }
    -basedir        where write .hhc and .hhk to
    -preview        no make and clean

=cut
use strict;
#use cmt::i18n();
use cmt::path;
use cmt::pp('ppvar');
use cmt::time('cdatetime');
use cmt::util('writefile');
use Data::Dumper;
use Exporter;
use YAML;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_appname        = $opt_verbtitle;
our $opt_templates;
    $opt_templates      = Load(join('', <DATA>));

our $DEFAULT_OUTPUT     = 'a.chm';
our $DEFAULT_PRJFILE    = 'a.hhp';
our $DEFAULT_TOCFILE    = 'toc.hhc';
our $DEFAULT_IDXFILE    = 'index.hhk';
our $DEFAULT_TITLE      = 'cmt::chm example';

our @ISA    = qw(Exporter);
our @EXPORT = qw(htmlinfo
                 dump_hhp
                 dump_hhc
                 dump_hhk
                 chm_compile
                 htabindent
                 prefix_compact
                 autogen_index
                 );

sub dump_hhp;
sub dump_hhc;
sub dump_hhc_node;
sub dump_hhk;

sub htabindent;
sub prefix_compact;
sub autogen_index;

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

sub parse_attributes {
    my $s = shift || $_;
    while (/((?:\w|-)+)\s*=\s*(".*?"|'.*?'|\S+)/g) {
        my ($k, $v) = ($1, $2);
        $v = $2 if $v =~ /^(["'])(.*?)\1$/;
        push @_, [$k, $v];
    }
    return @_;
}

=head2 htmlinfo

Parse HTML document and retrieve some important information.

The return value is a HASH-ref of:

    .title          TITLE
    .a              [ anchors... ]
    meta-name       meta-value

=cut
our %TAG_NOBREAK;
    $TAG_NOBREAK{$_} = 1 for qw(title h1 h2 h3 h4 h5 h6 h7);
sub htmlinfo {
    my $info    = {};
    my $tag;
    my $tagbuf;
    my $charset;
    pp {
        my $X = shift;
        $tagbuf .= $_ if !$X && defined $tag;
        return unless $X eq '<';
        $_ = hiconv($_, $charset) if defined $charset;
        if (/^\/((?:\w|-)+)/) {           # endtag: </tag>
            if ($tag eq lc $1) {
                if ($tag eq 'title') {
                    $tagbuf = hiconv($tagbuf, $charset) if defined $charset;
                    $info->{'.title'} = $tagbuf;
                } elsif ($tag =~ /^h\d+$/i) {
                    $tagbuf = hiconv($tagbuf, $charset) if defined $charset;
                    my $hX = '.'.lc($tag);
                    push @{$info->{$hX}}, $tagbuf;
                }
            } else {
                # ignore unmatched starttag/endtag
                # force to close the tag, exception for NOBREAK ones.
                return if defined $tag and $TAG_NOBREAK{$tag};
            }
            undef $tag;
            undef $tagbuf;
        } elsif (s/^((?:\w|-)+)//) {      # starttag: <tag ...
            return if defined $tag and $TAG_NOBREAK{$tag};
            $tag = lc $1;
            $tagbuf = '';
            if ($tag eq 'meta') {
                my ($nam, $htt, $cnt);
                for (parse_attributes) {
                    my ($k, $v) = (lc $_->[0], $_->[1]);
                    if ($k eq 'name') {
                        $nam = lc $v;
                    } elsif ($k eq 'content') {
                        $cnt = $v;
                    } elsif ($k eq 'http-equiv') {
                        $htt = lc $v;
                    }
                    if (defined $nam and defined $cnt) {
                        push @{$info->{$nam}}, $cnt;
                    } elsif (defined $htt and defined $cnt) {
                        if ($htt eq 'content-type') {
                            $charset = $1 if $cnt =~ /charset=((?:\w|-)+)/;
                        }
                    }
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

my %XMLENTS = (
    '"'     => '&quot;',
    # '\''    => '&apos;',
    '&'     => '&amp;',
    );
sub xml_value {
    my $v = shift || $_;
    $v =~ s/["'&]/$XMLENTS{$&}/g;
    return $v;
}

=pod

See Also:
    Sitemap formats:
        L<http://www.nongnu.org/chmspec/latest/Sitemap.html>
    Icon strip:
        L<http://west-wind.com/WebLog/posts/1520.aspx>

=cut
my %TYPEICON = qw(
    dir         5
    dir.index   1
    dir.pc      19
    dir.htab    1
    page        11
    anchor      17
    unknown     9
);
sub icon_index {
    my ($name) = shift;     # $_[0] =~ /^(\w+)/;
    $name = 'unknown' unless exists $TYPEICON{$name};
    $TYPEICON{$name};
}

sub sitemap {
    my ($name, $loc, $icon) = @_;
    $name =~ s/\s+/ /sg; # normalize-space, also remove newlines
    my $more = '<param name="ImageNumber" value="'.icon_index($icon).'">'
        if defined $icon;
    return '<LI><OBJECT type="text/sitemap"><param name="Name" value="'
            . xml_value($name) . '"><param name="Local" value="'
            . $loc . '">'.$more.'</OBJECT></LI>'."\n";
}

sub dump_hhp {
    my ($file, $cfg) = @_;
    my $fh;
    open($fh, '>', $file)
        or die("can't open $file to write: $!");

    my $files       = $cfg->{-files};
    $cfg->{-output}   ||= $DEFAULT_OUTPUT;
    $cfg->{-tocfile}  ||= $DEFAULT_TOCFILE;
    $cfg->{-idxfile}  ||= $DEFAULT_IDXFILE;
    # $cfg->{-default}  ||= $files->[0];
    $cfg->{-title}    ||= $DEFAULT_TITLE;
    $cfg->{-lang}     ||= '0x0804 Chinese';
    my %vars = (
        output      => $cfg->{-output},
        tocfile     => $cfg->{-tocfile},
        idxfile     => $cfg->{-idxfile},
        default     => $cfg->{-default},
        title       => $cfg->{-title},
        lang        => $cfg->{-lang},
    );

    my $inst = ppvar %vars, $opt_templates->{'chm.project'};
    print $fh $inst, "\n";
    print $fh "$_\n" for @$files;
    close $fh;
}

sub dump_hhc {
    my ($file, $roots) = @_;
    my $fh;
    open ($fh, '>', $file)
        or die("can't open $file to write: $!");
    print $fh "<HTML>\n<HEAD>\n";
    print $fh "<META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n";
    print $fh "</HEAD>\n<BODY>\n";
    print $fh "<UL>\n";
    dump_hhc_node($fh, $_) for @$roots[3..$#$roots];
    print $fh "</UL>\n</BODY>\n</HTML>\n";
    close $fh;
}

sub dump_hhc_node {
    my ($fh, $node) = @_;
    my ($file, $title, $attrs, @child) = @$node;
    print $fh sitemap($title, $file, $attrs->{'type'});
    if (@child) {
        print $fh "<UL>\n";
        dump_hhc_node($fh, $_) for @child;
        print $fh "</UL>\n";
    }
}

sub dump_hhk {
    my ($file, $idxmap) = @_;
    my $fh;
    open ($fh, '>', $file)
        or die("can't open $file to write: $!");
    print $fh "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML//EN\">\n<HTML>\n<BODY>\n";
    for (sort keys %$idxmap) {
        my $loc = $idxmap->{$_};
        if (ref $loc eq 'ARRAY') {
            for my $loc1 (@$loc) {
                print $fh sitemap($_, $loc1);
            }
        } else {
            print $fh sitemap($_, $loc);
        }
    }
    print $fh "</BODY>\n</HTML>\n";
    close $fh;
}

sub _hhc {
    my $prjfile = shift;
       $prjfile = $prjfile->{-prjfile} if ref $prjfile;

    my $mode = 0;
    if ($mode == 0) {
        exec "hhc \"$prjfile\"";
    } else {
        open (CAP, "hhc \"$prjfile\"|")
            or die "can't invoke hhc utility to do the compilation: $!";
        while (<CAP>) {
            print "hhc> $_";
        }
        close CAP;
        return 1;
    }
}

sub chm_compile {
    my %cfg     = @_;
    my $basedir = $cfg{-basedir} || '.';
    my $prjfile = $cfg{-prjfile};
    my $preview = $cfg{-preview};
    unless (defined $prjfile) {
        $prjfile = $DEFAULT_PRJFILE;
        if ($cfg{-output}) {
            my ($dir, $base) = path_split($cfg{-output});
            my ($file, $ext) = path_splitext($base);
            $prjfile = $file.'.hhp';
        }
        $cfg{-prjfile} = $prjfile;
    }

    my $g_prjfile = !-f $prjfile;
    unless (0) { #-f $prjfile) {
        info2 "writing $prjfile";
        dump_hhp $prjfile, \%cfg;
    }

    my $tocfile = $cfg{-tocfile} || $DEFAULT_TOCFILE;
    my $idxfile = $cfg{-idxfile} || $DEFAULT_IDXFILE;
    my $g_tocfile = !-f $tocfile;
    my $g_idxfile = !-f $idxfile;
    unless (0) { #-f $tocfile) {
        info2 "writing $tocfile";
        dump_hhc path_join($basedir, $tocfile), $cfg{-roots};
    }
    unless (0) { #-f $idxfile) {
        info2 "writing $idxfile";
        dump_hhk path_join($basedir, $idxfile), $cfg{-index};
    }
    return 1 if $preview;

    info2 "invoking hhc";
    my $ret;
       $ret = _hhc($prjfile);
    # unlink $prjfile if $g_prjfile;
    # unlink $tocfile if $g_tocfile;
    # unlink $idxfile if $g_idxfile;
    return $ret;
}

=head2 htabindent(NODE)

Indent children of NODE by C<htab> attribute.

When parsing HTML documents, the biggest H* tag appeared in the document is
considerred to be the document's "Head-Level" attribute. So if a document
contains <H2>, then its Head-Level is 2.

Generally, if a document has Head-Level defined, a C<htab>(Head-Indent)
attribute will be set to (Head-Level - 1).  The number of C<htab> is used to
determine the belongs-to relation between two sibling nodes.
Because the C<htab> of H1 documents is 0, so H1 documents and non-Head
documents are siblings, but H2 documents will be the children of H1 and non-Head
documents.

NOTICE: call C<htabindent> before C<prefix_compact>, because C<prefix_compact>
changes the sibling-relations of the children of NODE, and new compaction nodes
which don't have C<htab> attributes might be introduced.

NOTICE: after called C<htabindent>, the C<htab> attributes of all descendants
of NODE would be cleared.

=cut
sub htabindent {
    my $node = shift;
    return $node if @$node <= 3;
    my @tabparent;
    for (my $i = 3; $i < @$node; $i++) {
        my $sibling = $node->[$i];
        htabindent $sibling;

        my $tab = $sibling->[2]->{'htab'};
        unless (defined $tab) {
            # non-head siblings will separate htab-trees
            @tabparent = ();
            next
        }
        my $parent = $tabparent[$tab];
        unless (defined $parent) {
            for (my $j = $tab - 1; $j >= 0; $j--) {
                if (defined $tabparent[$j]) {
                    $parent = $tabparent[$j];
                    $tabparent[$tab] = $parent;
                    last
                }
            }
        }
        if (defined $parent) {
            info2 "htabindent $tab: ".$sibling->[1];
            my $htabrange = $parent->[2]->{'htabrange'}++;
            if ($htabrange == 0) {
                # add the first indented child, this is a chance to init
                # the parent node, to let it includes itself, to hide any
                # anchor nodes.
                my %copyattr = %{$parent->[2]};
                my $copy = [ @$parent[0..1], \%copyattr, splice(@$parent, 3) ];
                $copyattr{'iscopy_htabp'} = 1;
                $parent->[2]->{'type'} = 'dir.htab';
                push @$parent, $copy;
            }
            push @$parent, $sibling;
            splice @$node, $i--, 1;
        }
        delete $sibling->[2]->{'htab'};
        $tabparent[$tab + 1] = $sibling;
        splice @tabparent, $tab + 2;
    }
}

=head2 prefix_compact(NODE, PREFIX-PATTERN, MIN-PREFIX-REPEAT)

=cut
sub prefix_compact {
    my ($node, $pattern, $minrep) = @_;
    my @range;
    my ($prefix, $text);
    my $last_i = 3;
    my $lastprefix = undef;
    my $lasthtab;
    local $_;
    for (my $i = 3; $i < @$node; $i++) {
        my $child = $node->[$i];
        if (@$child > 3) {
            unless ($child->[3]->[0] =~ /#[^\/]+$/) {   # never compact anchors
                prefix_compact($child, $pattern, $minrep);
            }
        }

        $_ = $child->[1];
        $prefix = /$pattern/ ? $1 : $_;
        if ($prefix eq $lastprefix) {
            # check if compact across the htab-boundary
            next unless defined $lasthtab;
            if (defined (my $htab = $child->[2]->{'htab'})) {
                # children of the compacted region can indent as much as can,
                # but can't unindent too much.
                next if $htab >= $lasthtab;
            }
        }

        if ($i - $last_i >= $minrep) {
            push @range, [$lastprefix, $last_i, $i - $last_i];
        }
        $last_i = $i;
        $lastprefix = $prefix;
        $lasthtab = $child->[2]->{'htab'};
    }
    if (@$node - $last_i >= $minrep) {
        push @range, [$lastprefix, $last_i, @$node - $last_i];
    }
    while (@range) {
        my $range = pop @range;
        my ($prefix, $off, $len) = @$range;
        info2 "compact $prefix into a section. (off=$off len=$len)";
        my @compact = splice(@$node, $off, $len, undef);
        my $skipped = 0;
        for (my $i = 0; $i < @compact; $i++) {
            my $cnode = $compact[$i];
            my $ctitle = substr($cnode->[1], length($prefix));
            if ($ctitle eq '') {
                $ctitle = $cnode->[2]->{'head'};
                $ctitle = '<<'.($i - $skipped).'>>' if $ctitle eq '';
                $cnode->[1] = $ctitle;
                if (0) {    # DISABLED.
                    # merge the descendent (level 1)
                    my @merge_down = splice(@$cnode, 3);
                    splice(@compact, $i + 1, 0, @merge_down);
                    $skipped += @merge_down;
                    $i += @merge_down;
                }
            } else {
                $cnode->[1] = $ctitle;
            }
        }
        $prefix = $1 if $prefix =~ /$pattern/;
        $node->[$off] = [ $compact[0]->[0], $prefix, { type => 'dir.pc' },
                          @compact ];
    }
}

sub autogen_index {
    my $node = shift;
    my @addfiles;
    my $body;
    for (my $i = 3; $i < @$node; $i++) {
        my $child = $node->[$i];
        if (@$child > 3) {
            my $childanchor = $child->[3]->[0];
            if ($childanchor =~ /#[^\/]+$/) {
                # child is a file, so don't have to recursive into.
            } else {
                push @addfiles, autogen_index($child);
            }
        }
        my $file = $child->[0];
        my $title = $child->[1];
        my $size = -s $file;
        $body .= "<tr><td>$title</td><td><a href=\"$file\">$file</a></td>"
                ."<td>$size</td></tr>\n";
    }
    if (!defined $node->[0] and defined (my $dir = $node->[2]->{'dir'})) {
        my $indexfile = '$MKCHM_INDEX.html';
        my $file = $dir.'/'.$indexfile;
        info "auto generate index: $file";
        my %vars = (
            title   => $node->[1],
            body    => $body,
            total   => (@$node - 3),
            now     => cdatetime,
            appname => $opt_appname,
        );
        my $html = ppvar %vars, $opt_templates->{'autogen.index'};
        writefile $file, $html;
        $node->[0] = $file;
        push @addfiles, $file;
    }
    return @addfiles;
}

1
__DATA__

chm.project: |-
    [OPTIONS]
    Binary TOC=No
    Compatibility=1.1 or later
    Compiled file=$output
    Contents file=$tocfile
    Index file=$idxfile
    Default Window=Main
    Default topic=$default
    Display compile progress=Yes
    Full-text search=Yes
    ; Full text search stop list file=path
    Language=$lang
    Title=$title
    ; Enhanced decompilation=No

    [WINDOWS]
    ; WindowType=titlebar, toc, index, default, home,
    ;            jump-1, jump-1-text, jump-2, jump-2-text, navig-style,
    ;            navig-width, buttons_bit, "init-pos[l,t,r,b]", WS_style, EXWS_style,
    ;            grey-toolbars, navig-closed, default-navig(0=TOC,idx,search,fav,hist),
    ;            navig-pos(0=top,left,bottom), ID(WM_NOTIFY)
    Main="$title", "$tocfile","$idxfile","$default","$default",,,,,0x2520,,0x60384e,,,,,,,,0

    [FILES]

autogen.index: |-
    <html>
    <head>
        <title>Index Of $title</title>
        <meta name="generator" content="$VER">
        <style>
            p,h1,h2,h3,h4,th,td,cite {
                font-family: Times New Roman, Georgia, Courier New;
            }
            th {
                border-bottom: solid 1px black;
            }
        </style>
    </head>
    <body>
        <h1>Index of $title</h1>
        <hr>
        <table border="0">
        <tr>
            <th>Title</th>
            <th>File</th>
            <th>Size</th>
        </tr>
        $body
        <tr>
            <th colspan="3" align="left">Total $total entries.</th>
        </tr>
        </table>
        <hr>
        <cite style="text-align: right">
            Generated by $appname, updated at $now.
        </cite>
    </body>
    </html>
