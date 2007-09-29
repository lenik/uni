package Parse::Apache::Index;

=head1 NAME

Parse::Apache::Index - parse the Apache generated index page of file system

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 0;
use cmt::log(2);
use cmt::netutil('loadurl');
use cmt::util;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: Index.pm,v 1.1 2007-09-29 10:33:09 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use HTML::Entities;

our @ISA    = qw(Exporter);
our @EXPORT = qw(listdir_http
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use UnKnOwN;
    mysub(arguments...)

=head1 DESCRIPTION

B<UnKnOwN> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-UnKnOwN-RESOLVES.

=head1 OPTIONS

=cut
our $STRICT = 1;

=head1 FUNCTIONS

=cut
=head2 parsehtml(HTML)

=cut
sub parsehtml {
    my $html = shift;
    my $lasttag;
    my @index;
    while ($html =~ /<(\w+)\s+(.*?)>([^<]*)/sg) {
        my ($tag, $attrs, $text) = (lc($1), $2, $3);
        while (1) {
            if ($tag eq 'a') {
                last unless $lasttag eq 'img';
                # <img>... <a href="FILE">...
                last unless $attrs =~ /href\s*=\s*(?:"([^"]*)"|(\S+))/;
                my $href = defined $1 ? $1 : $2;
                last if substr($href, 0, 1) eq '?';
                $href = decode_entities($href);
                $text = decode_entities($text);
                last if $STRICT and $href ne $text;
                push @index, $href;
            }
            last
        }
        $lasttag = $tag;
    }
    @index
}

sub listdir_http {
    my $url = shift;
    my $html = loadurl($url);
    my @files;
    for (parsehtml $html) {
        my $name = ref $_ ? $_->[0] : $_;
        push @files, $name;
    }
    @files;
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