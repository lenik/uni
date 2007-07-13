package cmt::pp;

use strict;
use vars qw/@ISA @EXPORT/;
use Exporter;

@ISA    = qw(Exporter);
@EXPORT = qw(pp
	     ppcmt);

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

sub pp(&) {
    my $call = shift;
    # %exclude state:
    my $X;
    my @Xbuf;
    # (\\.|[^"])*"
    my $endc;
    my $cut;
    while (<>) {
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
            while (/\"|\'|\/\*/) {
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
    }
}

sub ppcmt(&) {
    my $call = shift;
    my $buf;
    pp {
        my $X = shift;
        if (defined $X) {
            $buf .= $X if $X ne '/*';
        } elsif (/\n$/s) {
            local $_ = $buf . $_;
            $call->();
            undef $buf;
        } else {
            $buf .= $_;
        }
    };
    if ($buf ne '') {
        local $_ = $buf;
        $call->();
    }
}

1