package cmt::oop;       # object-oriented parser

use strict;
use cmt::util;
use Exporter;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(oop_compile
                 oop
                 );

sub oop_compile {
    my $rwdef = shift;
    return 'yaccdef';
}

sub oop {
    my $rwdef = shift;
    my $text = shift;

    my $yaccdef = oop_compile($rwdef)
        or die "failed to compile rewrite-definition";
    my $parser; # = yapp_compile($yaccdef);
    # $parser->parse($text);
    # return $parser->yylval;
}

__DATA__

start:
    rules
  ;

rules:
    rule
  | rules rule
  ;

rule:
    symbol_name ruledef_op exp ';'
  ;

exp:
  CHAR
  | STRING
  | symbol_name
  | '\\' token_name
  | alias_name '=' symbol_name
  | '^' rwfmt_name
  | exp '|' exp
  | '[' exp ']'
  | '{' exp ',' exp '}'
  | exp exp
  | symbol_name '(' explist ')'
  ;

token_name:     WORD;
symbol_name:    WORD;
alias_name:     WORD;
rwfmt_name:     WORD;
ruledef_op:     ':' | RULEDEF_OP;
