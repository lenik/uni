package cmt::pp;

use strict;
use vars        qw(@ISA @EXPORT);
use cmt::atext;
use cmt::util;
# use Data::Dumper;
use Exporter;
use UNIVERSAL   qw(isa);

@ISA    = qw(Exporter);
@EXPORT = qw(pp
             ppcmt
             ppcmtstr
             ppdom
             ppvarf
             ppvar
             ppfmt_foobar);

my %PDEF = (    # Pairs
    '('         => ')',     # qr/.*?\)/,
    '['         => ']',     # qr/.*?\]/,
    '{'         => '}',     # qr/.*?\}/,
    '<'         => '>',     # qr/.*?\>/,
);

my %QDEF = (    # Quotes
    '"'         => qr/((?:\\.|[^"])*)"/,
    '\''        => qr/((?:\\.|[^'])*)'/,
    '`'         => qr/((?:\\.|[^`])*)`/,
    '/'         => qr/((?:\\.|[^\/])*)\//, # regex
    '%'         => qr/((?:\\.|[^%])*)%/,

    # NOT TESTED: """?
    'D"'        => qr/((?:""|[^"])*)"/,
    'D\''       => qr/((?:''|[^'])*)'/,
    'D`'        => qr/((?:``|[^`])*)`/,

    '/*'        => qr/(.*?)\*\//,
    '{*'        => qr/(.*?)\*\}/,       # WARNING: pascal comment is structured
    '(*'        => qr/(.*?)\*\)/,       # WARNING: pascal comment is structured
    '<?'        => qr/(.*?)\?>/,        # &gt; escape
    '<![CDATA[' => qr/(.*?)\]\]>/,      # &gt; escape
    '<'         => qr/(.*?)>/,          # &gt; escape

    '//'        => qr/(.*)$/,
    '--'        => qr/(.*)$/,
    '#'         => qr/(.*)$/,
);

sub pp(&;@) {
    my $call    = shift;
    my %cfg;      get_named_args @_, %cfg;
    my $qdef    = $cfg{-qdef} || \%QDEF;
    my $qset    = qr_literal($cfg{-qset} || '\'|"|#', 'o');
    my $rem     = $cfg{-rem};

    my $X;
    my @Xbuf;
    # (\\.|[^"])*"
    my $qend;
    my $proc = sub {
        if (defined $X) {
            if (s/^$qend//) {
                push @Xbuf, $rem ? $1 : $&;
                local $_ = join('', @Xbuf);
                $call->($X);
                undef $X;
                undef @Xbuf;
            } else {
                push @Xbuf, $_;
                return;
            }
        }
        # assert $X == undefined
        while (/$qset/) {
            $X = $&;
            $qend = $qdef->{$X};
            die "Unknown quote-char($X)" unless defined $qend;
            if ($` ne '') {
                local $_ = substr($_, 0, $-[0], '');
                $call->();
            }
            if (s/^$X$qend//) {
                local $_ = $rem ? $1 : $&;
                $call->($X);
                undef $X;
            } else {
                $_ = substr($_, length $X) if $rem;
                push @Xbuf, $_;
                undef $_;
            }
        }
        $call->() if $_ ne '';
    };

    if (scalar @_ == 0) {
        $proc->() while <>;
    } else {
        for my $d (@_) {
            if (UNIVERSAL::isa($d, 'GLOB')) {
                $proc->() while <$d>;
            } elsif (ref $d eq 'ARRAY') {
                $proc->() for @$d;
            } else {
                die "$d: Neither a glob nor an array-ref";
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
    } -qset => q('|"|/*|#), @_;
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
    } -qset => q('|"|/*|#), @_;
}

sub _errdie { die shift }
sub _expar  { my $x = shift; ref $x eq 'ARRAY' ? @$x : ($x) }
sub ppdom(&;@) {
    my $call    = shift;
    my %cfg;      get_named_args @_, %cfg;
    my $pdef    = $cfg{-pdef} || \%PDEF;
    my $pset    = $cfg{-pset} || [ keys %$pdef ];
       $pset    = [ split(/\|/, $pset) ] unless ref $pset; # '(|[|{'
    my $pset2   = qr_literal(join('|', @$pset, @$pdef{@$pset}), 'o');
    my $rem     = $cfg{-rem};
    my $errf    = $cfg{-err} || \&_errdie;
    delete $cfg{-pdef};
    delete $cfg{-pset};
    @_ = [@_] if @_;

    my @Xst;                # X-stack
    my @stack;              # node-stack
    my $curr    = atext(''); # current-node
    pp {
        my $X = shift;  # NOTE: This is quote-char, not pair-char
        if (defined $X) {
            local $_ = atext_tag($X, $_);
            $call->($X);
            $curr = $curr->cat(_expar $_);
            return;
        }
        my $X0 = $Xst[-1];
        while (/$pset2/) {
            $X = $&;
            if (exists $pdef->{$X}) {   # left-open
                my $pend = $pdef->{$X};
                $errf->("Unknown pair-char($X)") unless defined $pend;
                if ($` ne '') {
                    local $_ = substr($_, 0, $-[0], '');
                    $call->($X0);       # ^<...>[...
                    $curr = $curr->cat(_expar $_);
                }
                push @Xst, $X;
                push @stack, $curr;
                local $_ = substr($_, 0, length($X), '');
                $call->($X) unless $rem; # ...<[>...
                $curr = $rem ? atext_tag($X) : atext_tag($X, _expar $_);
                $X0 = $X;
            } else {                    # right-close
                $X0 = pop @Xst;
                my $expect = $pdef->{$X0};
                $errf->("Expected $expect, but got $X") if $expect ne $X;
                local $_ = substr($_, 0, $+[0], '');
                      $_ = substr($_, 0, $-[0]) if $rem;
                if ($_ eq '') {
                    $_ = $curr;         # ^<]>...
                } else {
                    $call->($X0);       # ^<...]>...
                    $_ = $curr->cat(_expar $_);
                }
                $call->($X0);           # a node is made up
                $curr = pop @stack;
                $curr = $curr->cat(_expar $_);
                $X0 = $Xst[-1];
            }
        }
        if ($_ ne '') {
            $call->($X0);               # ...]<...>$
            $curr = $curr->cat(_expar $_);
        }
    } %cfg, @_;
    $errf->("Pending pairs: ".join('..', @Xst)) if @Xst;
    return @stack ? $stack[0] : $curr;
}

sub ppvarf(&$) {
        no warnings('substr', 'uninitialized');
    my $resolv  = shift;
    my $text    = shift;
    forx qr/\$(\w+|\{(\\.|[^\}])*\}|\S)/, sub {
        if (substr($_, $-[0] - 1, 1) ne '\\') {
            my $name = (substr($1, 0, 1) eq '{')
                ? substr($1, 1, length($1) - 2) : $1;
            my $value = $resolv->($name);
            $_ = $value if defined $value;
        }
    }, $text;
}

sub ppvar(\%@) {
    my $vars = shift;
    ppvarf sub { my $k = shift; my $v; return undef unless exists $vars->{$k};
                 defined ($v = $vars->{$k}) ? $v : '' }, join('', @_);
}

# [$artist - ][$album - [%track number% - ]]$title
sub ppfmt_foobar(&$) {
    my $resolv  = shift;
    my $text    = shift;
    my $root    = ppdom {
        if (isa $_, 'cmt::atext::Tag') {
            if ($_->tag eq '%') {
                $_ = atext_call $resolv, $_->val;
            } elsif ($_->tag eq '[') {
                $_ = atext_cat # this prefix made Gat->val never undef.
                     atext_gat @$_[1..$#$_];
            }
        }
        return if ref $_;
        my @decomp;
        my $apply;
        forx qr/\$(\w+|\{.*?\}|\S)/, sub {
            push @decomp, atext_call($resolv, $1);
            $apply = 1;
        }, sub {
            push @decomp, $_
        }, $_;
        $_ = \@decomp if $apply;
    } -qset => q('|"|%),
      -pset => ['[', '('],
      -rem => 1, $text;
}

1