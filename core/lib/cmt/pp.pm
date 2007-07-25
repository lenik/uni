package cmt::pp;

use strict;
use vars qw/@ISA @EXPORT/;
use cmt::util;
# use Data::Dumper;
use Exporter;

@ISA    = qw(Exporter);
@EXPORT = qw(pp
             ppcmt
             ppcmtstr
             ppvar);

my %ENDC = (
    '"'         => qr/(\\.|[^"])*"/,
    '\''        => qr/(\\.|[^'])*'/,
    '`'         => qr/(\\.|[^`])*`/,
    '/'         => qr/(\\.|[^\/])*\//,  # for regex

    # NOT TESTED: """?
    'D"'        => qr/(""|[^"])*"/,
    'D\''       => qr/(''|[^'])*'/,
    'D`'        => qr/(``|[^`])*`/,

    '/*'        => qr/.*?\*\//,
    '{*'        => qr/.*?\*\}/,         # WARNING: pascal comment is structured
    '(*'        => qr/.*?\*\)/,         # WARNING: pascal comment is structured
    '<?'        => qr/.*?\?>/,          # &gt; escape
    '<![CDATA[' => qr/.*?\]\]>/,        # &gt; escape
    '<'         => qr/.*?>/,            # &gt; escape

    '//'        => qr/.*$/,
    '--'        => qr/.*$/,
    '#'         => qr/.*$/,
);

sub pp(&;@) {
    my $call = shift;
    my %cfg; get_named_args @_, %cfg;
    my $tok  = qr_literal($cfg{tok} || '\'|"|/*', 'o');

    my $X;
    my @Xbuf;
    # (\\.|[^"])*"
    my $endc;
    my $cut;
    my $proc = sub {
        if (defined $X) {
            if (s/^($endc)//) {
                push @Xbuf, $1;
                local $_ = join('', @Xbuf);
                $call->($X);
                undef $X;
                undef @Xbuf;
            } else {
                push @Xbuf, $_;
            }
        }
        if (! defined $X) {
            while (/$tok/) {
                $X = $&;
                $endc = $ENDC{$X};
                die "Unknown X-begin($X)" unless defined $endc;
                if ($` ne '') {
                    my $cut = $`;
                    $_ = substr($_, $-[0]);
                    local $_ = $cut; #$`;
                    $call->();
                }
                if (s/^($X$endc)//) {
                    local $_ = $1;
                    $call->($X);
                    undef $X;
                } else {
                    push @Xbuf, $_;
                    undef $_;
                }
            }
            $call->() if $_ ne '';
        }
    };

    if (scalar @_ == 0) {
        $proc->() while <>;
    } else {
        for my $d (@_) {
            if (UNIVERSAL::isa($d, 'GLOB')) {
                $proc->() while <$d>;
            } else {
                $proc->() for @$d;
            }
        }
    }
}

sub ppcmt(&;@) {
    my $call = shift;
    @_ = [@_] if @_;
    my $buf;
    pp {
        my $X = shift;
        if (defined $X) {
            if ($X eq '/*' or $X eq '#') {
                # comments
            } else {
                $buf .= $X;
            }
        } elsif (/\n$/s) {
            local $_ = $buf . $_;
            $call->();
            undef $buf;
        } else {
            $buf .= $_;
        }
    } -tok => q('|"|/*|#), @_;
    if ($buf ne '') {
        local $_ = $buf;
        $call->();
    }
}

sub ppcmtstr(&;@) {
    my $call = shift;
    @_ = [@_] if @_;
    pp {
        my $X = shift;
        if (defined $X) {
            return if ($X eq '/*' or $X eq '#');
            $_ = substr($_, 1, length($_) - 2);
            return $call->($X);
        }
        for (split(/\s+/, $_)) {
            next unless /\S/;
            $call->()
        }
    } -tok => q('|"|/*|#), @_;
}

sub ppvar(&$) {
    my $resolv  = shift;
    my $text    = shift;
    forx qr/\$(\w+|\{.*?\}|\S)/, sub {
        if (substr($text, $-[0] - 1, 1) ne '\\') {
            my $name = $1;
            my $value = $resolv->($name);
            $_ = $value if defined $value;
        }
    }, $text;
}

1