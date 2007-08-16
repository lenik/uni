
%token      _char _string _id
%token      _rw_cntl
%token      _ruledef_op _sect_delim

%nonassoc   _char _string _id _rw_cntl _code
%nonassoc   '(' ')'
%nonassoc   '%'

%left       '|'
%left       ','
%nonassoc   _cc_low
%nonassoc   _cc
%nonassoc   _repeat
%left       '/'
%right      '='
%nonassoc   '{' '}'
%nonassoc   '?' '*' '+'


%{
    my @S;

    sub _H($) { $_[0] }
    sub _J {
        my $t = shift;
        $t eq $_[0]->[0]
            ? [@{$_[0]}, $_[1]]
            : $t eq $_[1]->[0]
                ? [$t, $_[0], @{$_[1]}[1..$#{$_[1]}]]
                : [$t, $_[0], $_[1]]
    }
    sub _M { _H { map { $_ => 1 } @_ } }
    sub _S { push @S, $_[0]; $_[0] }
%}
%%

start:
    header rules footer             { { 'header' => $_[1],
                                        'rules'  => $_[2],
                                        'footer' => $_[3],
                                        'seq'    => [ @S ],
                                        'seqm'   => _M @S } }
  ;

header:
    text _sect_delim                { undef @S; $_[1] }
  ;

footer:
    _sect_delim text                { $_[2] }
  ;

text:
                                    { '' }
  | textl                           { join('', @{$_[1]}) }
  ;

textl:
    _string                         { [$_[1]] }
  | textl _string                   { [@{$_[1]}, $_[2]] }
  ;

rules:
    rule                            { _H { _S($_[1]->[0]) => $_[1]->[1] } }
  | rules rule                      { $_[1]->{_S($_[2]->[0])} = $_[2]->[1];
                                      $_[1] }
  ;

rule:
    symbol_name ruledef_op ruledefs ';'
                                    { [ $_[1], $_[3] ] }
  ;

ruledefs:
    ruledef
  | ruledefs '|' ruledef            { _J('or',      $_[1], $_[3]) }
  ;

ruledef:                            { ['empty'] }
  | nonempty
  ;

nonempty:
    ruleexp
  | concat                          { ['concat',    @{$_[1]}] }
  ;

concat:
    ruleexp ruleexp %prec _cc_low   { [ $_[1], $_[2] ] }
  | concat ruleexp %prec _cc        { [ @{$_[1]}, $_[2] ] }
  ;

ruleexp:
    term
  | symbol_name                     { ['ref',       $_[1]] }
  | _code                           { ['code',      $_[1]] }
  | quantifiers
  | call
  | '(' ruledefs ')'                { ['group',     $_[2]] }
  | alias_name '=' ruleexp          { ['alias',     $_[1], $_[3]] }
  | ruleexp '/' alias_name          { ['alias',     $_[3], $_[1]] }
  | rw_exp
  | spec
  ;

term:
    _char                           { ['char',      $_[1]] }
  | _string                         { ['string',    $_[1]] }
  ;

quantifiers:
    ruleexp '?'                     { ['qt',        $_[1], 0, 1] }
  | ruleexp '*'                     { ['qt',        $_[1], 0] }
  | ruleexp '+'                     { ['qt',        $_[1], 1] }
  | ruleexp '{' range '}'           { ['qt',        $_[1], @{$_[3]}] }
  | '{' ruledefs ',' nonempty '}' %prec _repeat
                                    { ['repeat',    $_[2], $_[4]] }
  ;

range:
    _number                         { [ $_[1], $_[1] ] }
  | _number '.' '.'                 { [ $_[1], ] }
  | '.' '.' _number                 { [ 0, $_[3] ] }
  | _number '.' '.' _number         { [ $_[1], $_[4] ] }
  ;

call:
    function_name '<' explist '>'   { ['call',      $_[1], @{$_[3]}] }
  ;

explist:
    ruleexp                         { [$_[1]] }
  | explist ',' ruleexp             { [@{$_[1]}, $_[3]] }
  ;

rw_exp:
    _rw_cntl                        { ['rw_cntl',   $_[1]] }
  ;

spec:
    '%' _id _id                     { ['raw',       '%'.$_[2].' '.$_[3]] }
  | '%' _id _char                   { ['raw',       '%'.$_[2]." '$_[3]'"] }
  ;

symbol_name:    _id;
alias_name:     _id;
function_name:  _id;
ruledef_op:     ':' | _ruledef_op;

%%
