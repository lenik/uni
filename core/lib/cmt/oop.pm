package cmt::oop;       # object-oriented parser

use strict;
use cmt::lexutil;
use cmt::oop_y;
use cmt::oop_mod;
use cmt::util;
use Data::Dumper;
use Exporter;
use Parse::Lex;
use Parse::Yapp;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA        = qw(Exporter);
our @EXPORT     = qw(oop_compile
                     oop_parse
                     oop_conv
                     oop);

sub _lexdump {
    my $lexer = shift;
    my $next = shift || sub {
        my $t = $lexer->next;
        if ($lexer->eoi || !defined $t) {
            ('', undef)
        } else {
            ($t->name, $t->text)
        }
    };
    while (my @ret = $next->()) {
        last if $ret[0] eq '';
        printf "%-10s '%s'\n", @ret;
    }
}

sub oop_parse {
    my $f = shift;

    my $bl = 0;
    my $cbuf;
    my $lexer = new Parse::Lex;
    $lexer->exclusive('header'=>1, 'footer'=>1, 'code'=>1);
    $lexer->skip('\s*#(?s:.*)|\s+');
    $lexer->defineTokens(
        qw(header:_del_h %%\n)
                        => sub { $lexer->end('header'); ['sect_delim','%%']},
        qw(header:_str_h .*\n)
                        => sub { [ 'string', $_[1] ] },
        qw(footer:_str_f .*(\n|$))
                        => sub { [ 'string', $_[1] ] },
        qw(code:_br_c \?>)
                        => sub { if (--$bl) { $cbuf .= $_[1]; '__br_' } else
                                 { $lexer->end('code'); [ 'code', $cbuf ] } },
        qw(code:__bl_ <\?)
                        => sub { ++$bl; $cbuf .= $_[1] },
        qw(code:__code [^<?>]+|\n)
                        => sub { $cbuf .= $_[1] },
        qw(__bl <\?)    => sub { ++$bl; $cbuf = ''; $lexer->start('code') },
        qw(_del_n %%\n) => sub { $lexer->start('footer');['sect_delim','%%']},
        qw(
            id          [a-zA-Z_][a-zA-Z_0-9]*
            number      \d+
            rw_cntl     \^[a-zA-Z_][a-zA-Z_0-9]*
            ruledef_op  ::=
        ),
        qw(char),   [qw(' (?:\\.|[^'])* ')] => sub { substr($_[1], 1, -1) },
        qw(string), [qw(" (?:\\.|[^"])* ")] => sub { substr($_[1], 1, -1) },
        qw(
            _op         .
        ),
    );
    $lexer->from($f);
    $lexer->start('header');

    my $yylex = yylex2 $lexer;
    # _lexdump($lexer, $yylex); return;

    my $parser = new cmt::oop_y;
    my $dom = $parser->YYParse(
        yylex =>
            # sub { my @r = $yylex->(); print "-- ",join('->',@r),"\n"; @r },
            $yylex,
        yyerror => sub {
            my ($tok,$val)=$parser->YYLexer->();
            die "unexpected token: $tok (value=$val)\n";
        },
    );
    return $dom;
}

sub oop_conv {
    my ($dom, $mod) = @_;
    if (defined $mod) {
        $mod->add_ruledefs($dom->{'rules'});
    } else {
        $mod = new cmt::oop_mod($dom);
    }
    my ($yylex, $lexer) = $mod->newlexer;
    {
        'mod'   => $mod,
        'y'     => $mod->dump,
        'yylex' => $yylex,
        'lexer' => $lexer,
    };
}

sub oop_compile {
    my $f = shift;
    my $dom = oop_parse $f;
    my $mod = oop_conv($dom, @_);
    my ($yylex, $lexer)$mod->newlexer
}

sub oop {
    my $f = shift;
    my $text = shift;

    my $conv = oop_compile($f)
        or die "failed to compile rewrite-definition";
    my $parser; # = yapp_compile($yaccdef);
    # $parser->parse($text);
    # return $parser->yylval;
}

1