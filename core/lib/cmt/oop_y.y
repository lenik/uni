
%token      _char _string _id
%token      _rw_cntl
%token      _ruledef_op _sect_delim

%left       '|'
%left       ','
%left       _cc
%right      '='
%right      '?' '*' '+'

%nonassoc   _char _string _id _rw_cntl _code
%nonassoc   '(' ')' '{' '}'

%%

start:
    header rules footer             { { 'header' => $_[1],
                                        'rules'  => $_[2],
                                        'footer' => $_[3], } }
  ;

header:
    text _sect_delim                { $_[1] }
  ;

footer:
  | _sect_delim                     { '' }
  | _sect_delim text                { $_[2] }
  ;

rules:
    rule
  | rules rule                      { my $h = {%{$_[1]}, %{$_[2]}} }
  ;

rule:
    symbol_name ruledef_op rule_exp ';'
                                    { my $h = { $_[1] => $_[3] } }
  ;

rule_exp:
    term
  | symbol_name                     { ['ref',       $_[1]] }
  | rw_exp
  | _code                           { ['code',      $_[1]] }
  | quantifiers
  | call
  | '(' rule_exp ')'                { ['group',     $_[2]] }
  | alias_name '=' rule_exp         { ['alias',     $_[1], $_[3]] }
  | rule_exp rule_exp %prec _cc     { $_[1]->[0] eq 'concat'
                                        ? [@{$_[1]}, $_[2]]
                                        : ['concat',$_[1], $_[2]] }
  | rule_exp '|' rule_exp           { $_[1]->[0] eq 'or'
                                        ? [@{$_[1]}, $_[3]]
                                        : ['or',    $_[1], $_[3]] }
  ;

term:
    _char                           { ['char',      $_[1]] }
  | _string                         { ['string',    $_[1]] }
  ;

rw_exp:
    _rw_cntl                        { ['rw_cntl',   $_[1]] }
  ;

quantifiers:
    rule_exp '?'                    { ['q',         $_[1], 0, 1] }
  | rule_exp '*'                    { ['q',         $_[1], 0] }
  | rule_exp '+'                    { ['q',         $_[1], 1] }
  | '{' rule_exp ',' rule_exp '}'   { ['repeat',    $_[2], $_[4]] }
  ;

call:
    function_name '<' explist '>'   { ['call',      $_[1], @{$_[3]}] }
  ;

explist:
    rule_exp                        { [$_[1]] }
  | explist ',' rule_exp            { [@{$_[1]}, $_[3]] }
  ;

text:
    _string                         { [$_[1]] }
  | text _string                    { [@{$_[1]}, $_[2]] }
  ;

symbol_name:    _id;
alias_name:     _id;
function_name:  _id;
ruledef_op:     ':' | _ruledef_op;

%%
