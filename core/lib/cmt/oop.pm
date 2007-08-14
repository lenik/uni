package cmt::oop;       # object-oriented parser

use strict;
use cmt::lexutil;
use cmt::oop_y;
use cmt::oop_mod;
use cmt::path;
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

sub y_compile {
    my $ydef = shift;
    my $cls = shift || 'OOP_'.int(10000 * rand);
    my $yp = new Parse::Yapp(input => $ydef);
    my $warnings = $yp->Warnings();
    my $conflicts = $yp->Conflicts();
    my $verbinfo;
    if ($warnings or $conflicts) {
        my $lin = "---------------------------------\n";
        $verbinfo = "Rules: \n$lin".$yp->ShowRules
                  . "\nDFA: \n$lin".$yp->ShowDfa
                  . "\nSummary: \n$lin".$yp->Summary;
    }
    my $qcls = __PACKAGE__.'::'.$cls;
    my $src = $yp->Output(
        classname   => $qcls,
        standalone  => 0,
        linenumbers => 0,
        template    => undef
        );

    # BUGFIX to Parse::Yapp, the SUPER::new doesn't work
    $src =~ s/^(\@ISA)/our \1/m;
    $src = $src."__PACKAGE__->new";

    my $v = eval $src;
    if ($@ ne '' or !$v) {
        my $tmp = temp_path $cls.".pm";
        writefile $tmp, $src;
        die "failed to compile $tmp: $@";
    }
    wantarray ? ($qcls, $warnings, $conflicts, $verbinfo) : $qcls
}

sub y_newparser {
    my $ydef = shift;
    my $lexer = shift;
    my ($cls, $warnings, $conflicts, $verbinfo) = y_compile $ydef;
    print STDERR $warnings  if $warnings;
    print STDERR $conflicts if $conflicts;
    print STDERR $verbinfo  if $verbinfo;
    my $parser = new $cls;
    # print "Created the parser for generated yacc-def: $parser\n";
    sub {
        my $yylex = $lexer->(shift);
        $parser->YYParse(
            yylex => $yylex,
            yyerror => \&y_error,
        )
    }
}

sub y_error {
    my $parser = shift;
    my $cls = ref $parser;
    my $tok = $parser->YYCurtok;
    my $val = $parser->YYCurval;
    die "(Parser $cls) unexpected token: $tok (value=$val)";
}

sub oop_parse {
    my $f = shift;

    my $bl = 0;
    my $cbuf;
    my $lex = new Parse::Lex;
    $lex->exclusive('header'=>1, 'footer'=>1, 'code'=>1);
    # $lex->skip('\s*#(?s:.*)|\s+');
    $lex->defineTokens(
        qw(header:_del_h %%\n)
                        => sub { $lex->end('header'); ['sect_delim','%%']},
        qw(header:_str_h .*\n)
                        => sub { [ 'string', $_[1] ] },
        qw(footer:_str_f .*(\n|$))
                        => sub { [ 'string', $_[1] ] },
        qw(code:_br_c \?>)
                        => sub { if (--$bl) { $cbuf .= $_[1]; '__br_' } else
                                 { $lex->end('code'); [ 'code', $cbuf ] } },
        qw(code:__bl_ <\?)
                        => sub { ++$bl; $cbuf .= $_[1] },
        qw(code:__code ([^<?]|<[^?]|\?[^>]|\n)+)
                        => sub { $cbuf .= $_[1] },
        qw(__bl <\?)    => sub { ++$bl; $cbuf = ''; $lex->start('code') },
        qw(_del_n %%\n) => sub { $lex->start('footer');['sect_delim','%%']},
        qw(
            id          [a-zA-Z_][a-zA-Z_0-9]*
            number      \d+
            rw_cntl     \^[a-zA-Z_][a-zA-Z_0-9]*
            ruledef_op  ::=
        ),
        qw(char),   [qw(' (?:\\.|[^'])* ')] => sub { substr($_[1], 1, -1) },
        qw(string), [qw(" (?:\\.|[^"])* ")] => sub { substr($_[1], 1, -1) },
        qw(
            __comment   #.*
            __space     \s+
            _op         .
        ),
    );

    my $lexer = yylex2 $lex;
    my $yylex = $lexer->($f);
    $lex->start('header');
    # _lexdump($lexer, $yylex); return;

    my $parser = new cmt::oop_y;
    my $dom = $parser->YYParse(
        yylex =>
            # sub { my @r = $yylex->(); print "-- ",join('->',@r),"\n"; @r },
            $yylex,
        yyerror => \&y_error,
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
    $mod
}

sub oop_compile {
    my $f = shift;
    my $dom = oop_parse $f;
    my $mod = oop_conv($dom, @_);
    my $ydef = $mod->dump($dom);
    my $lexer = $mod->newlexer;
    wantarray ? ($ydef, $lexer) : $ydef
}

sub oop_newparser {
    my ($ydef, $lexer) = oop_compile @_;
    # return sub { _lexdump(undef, $lexer->(shift)); exit -1 };
    my $yparser = y_newparser($ydef, $lexer);
    sub {
        $yparser->(shift)
    }
}

sub oop {
    my ($f, $text) = @_;
    my $parser = oop_newparser $f;
    $parser->($text)
}

1