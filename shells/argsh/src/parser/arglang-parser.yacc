%require "3.2"
// %glr_parser

%code requires {
    #include "argdom.h"
    using namespace arglang;
}

%{

    #include <string>

    int yylex(void *) {
        return 0;
    }
%}

%define api.value.type variant

%token<LexToken *> INTEGER
%token<LexToken *> FLOAT
%token<LexToken *> STRING
%token<LexToken *> ID UNICODE_ID VARREF
%token<LexToken *> ARG COMMAND
%token<LexToken *> EOL

%token<LexToken *> IF ELSE FOR DO WHILE IN
%token<LexToken *> BREAK CONTINUE RETURN
%token<LexToken *> SWITCH CASE DEFAULT
%token<LexToken *> VAR EVAL CLASS STATIC

%precedence _nop
%left EOL ';'

%precedence IF FOR WHILE SWITCH
%precedence THEN
%precedence ELSE

%precedence redir
%left<LexToken *> N_LT N_GT N_GTGT N_PIPE

%left '='
%left '<' '>' LEQ GEQ EQ NEQ
%left LTLT GTGT
%left '&' '|'
%left '+' '-'
%left '*' '/'
%left '^'

%nterm<Argument *>          argument
%nterm<Arguments *>         arguments
%nterm<BinaryExpr *>        binary_expr
%nterm<CmdLine *>           cmdline
%nterm<EvalStatement *>     eval_expr
%nterm<Expr *>              expr
%nterm<FileSpec *>          file_spec
%nterm<Expr *>              for_condition
%nterm<Expr *>              while_condition
%nterm<ForStatement *>      for_loop
%nterm<FunctionCall *>      fncall
%nterm<IfStatement *>       if_else
%nterm<Identifier *>        identifier
%nterm<LiteralValue *>      literal_value
%nterm<Parameter *>         parameter
%nterm<Parameters *>        parameters
%nterm<Program *>           program
%nterm<RedirectFragment *>  redir_fragment
%nterm<RedirectFragments *> redir_fragments
%nterm<RedirectStatement *> redirect
%nterm<Statement *>         flow_control
%nterm<Statement *>         for_init for_iteration
%nterm<Statement *>         seq_statements
%nterm<Statements *>        statements
%nterm<Statement *>         statement
%nterm<SwitchCase *>        case_entry
%nterm<SwitchCases *>       case_list
%nterm<SwitchStatement *>   switch_case
%nterm<VarDeclarations *>   var_decl
%nterm<VarDeclaration *>    var_init
%nterm<VarRef *>            var_ref
%nterm<WhileStatement *>    while_loop

%%

program:
    statements              { $$ = new Program($1); }
    ;

statements:
    statement               { $$ = Statements::one($1); }
    | statements st_seps statement
                            { $$ = $1->add_move($3); }
    ;

st_seps:
    st_sep
    | st_seps st_sep
    ;
    
st_sep:
    EOL
    | ';'
    ;

statement:
    seq_statements          { $$ = $1; }
    | var_decl              { $$ = $1; }
    | eval_expr             { $$ = $1; }
    | cmdline               { $$ = $1; }
    | redirect              { $$ = $1; }
    | fncall                { $$ = new EvalStatement($1); }
    | if_else               { $$ = $1; }
    | for_loop              { $$ = $1; }
    | while_loop            { $$ = $1; }
    | switch_case           { $$ = $1; }
    | flow_control          { $$ = $1; }
    ;

seq_statements:
    '{' statements '}'      { $$ = $2; }
    ;

eval_expr:
    EVAL '(' expr ')'       { $$ = new EvalStatement($3); }
    ;

expr:
    literal_value           { $$ = $1; }
    | var_ref               { $$ = $1; }
    | identifier            { $$ = new VarRef($1); }
    | fncall                { $$ = $1; }
    | binary_expr           { $$ = $1; }
    | '(' expr ')'          { $$ = $2; }
    ;

binary_expr:
    expr '+' expr           { $$ = BinaryExpr::add_move($1, $3); }
    | expr '-' expr         { $$ = BinaryExpr::subtract($1, $3); }
    | expr '*' expr         { $$ = BinaryExpr::multiply($1, $3); }
    | expr '/' expr         { $$ = BinaryExpr::divide($1, $3); }
    | expr '&' expr         { $$ = BinaryExpr::_and($1, $3); }
    | expr '|' expr         { $$ = BinaryExpr::_or($1, $3); }
    | expr '^' expr         { $$ = BinaryExpr::_xor($1, $3); }
    | expr '=' expr         { $$ = BinaryExpr::assign($1, $3); }
    | expr '<' expr         { $$ = BinaryExpr::lessThan($1, $3); }
    | expr '>' expr         { $$ = BinaryExpr::greaterThan($1, $3); }
    | expr LEQ expr         { $$ = BinaryExpr::lessOrEquals($1, $3); }
    | expr GEQ expr         { $$ = BinaryExpr::greaterOrEquals($1, $3); }
    | expr EQ expr          { $$ = BinaryExpr::equals($1, $3); }
    | expr NEQ expr         { $$ = BinaryExpr::notEquals($1, $3); }
    | expr LTLT expr        { $$ = BinaryExpr::lshift($1, $3); }
    | expr GTGT expr        { $$ = BinaryExpr::rshift($1, $3); }
    ;

literal_value:
    INTEGER                 { $$ = new IntegerLiteral($1); }
    | FLOAT                 { $$ = new FloatLiteral($1); }
    | STRING                { $$ = new StringLiteral($1); }
    ;

cmdline:
    COMMAND                 { $$ = new CmdLine($1); }
    | COMMAND arguments     { $$ = new CmdLine($1, $2); }
    ;

parameters:
    parameter               { $$ = Parameters::one($1); }
    | parameters ',' parameter
                            { $$ = $1->add_move($3); }
    ;

parameter:
    expr                    { $$ = Parameter::expr($1); }
    ;

arguments:
    argument                { $$ = Arguments::one($1); }
    | arguments argument    { $$ = $1->add_move($2); }
    ;

argument:
    literal_value           { $$ = Argument::literal($1); }
    | var_ref               { $$ = Argument::varRef($1); }
    | identifier            { $$ = Argument::identifier($1); }
    | ARG                   { $$ = Argument::rawChars($1); }
    ;
    
redirect:
    statement redir_fragments %prec redir
                            { $$ = $1->redirect($2); }
    ;

redir_fragments:
    redir_fragment          { $$ = RedirectFragments::one($1); }
    | redir_fragments redir_fragment
                            { $$ = $1->add_move($2); }
    ;

redir_fragment:
    '>' file_spec           %prec redir
                            { $$ = RedirectFragment::outputTo($2); }
    | GTGT file_spec        { $$ = RedirectFragment::appendTo($2); }
    | N_GT file_spec        { $$ = RedirectFragment::outputTo($2, $1->integral); }
    | N_GTGT file_spec      { $$ = RedirectFragment::appendTo($2, $1->integral); }
    | '<' file_spec         %prec redir
                            { $$ = RedirectFragment::inputFrom($2); }
    | N_LT file_spec        { $$ = RedirectFragment::inputFrom($2, (int) $1->integral); }
    | '|' statement         %prec redir
                            { $$ = RedirectFragment::pipeTo($2); }
    | N_PIPE statement      { $$ = RedirectFragment::pipeTo($2, (int) $1->integral); }
    ;

file_spec:
    INTEGER                 { $$ = new FileSpec($1); }
    | FLOAT                 { $$ = new FileSpec($1); }
    | STRING                { $$ = new FileSpec($1); }
    | ARG                   { $$ = new FileSpec($1); }
    ;

fncall:
    identifier '(' parameters ')'
                            { $$ = new FunctionCall($1, $3); }
    ;

if_else:
    IF '(' expr ')' statement %prec THEN
                            { $$ = new IfStatement($3, $5); }
    | IF '(' expr ')' statement ELSE statement %prec IF
                            { $$ = (new IfStatement($3, $5))->else_move($7); }
    ;

for_loop:
    FOR '(' for_init ';' for_condition ';' for_iteration ')' statement %prec FOR
                            { $$ = ForStatement::c($3, $5, $7)->do_move($9); }
    | FOR identifier IN '(' expr ')' statement %prec FOR
                            { $$ = ForStatement::each($2, $5)->do_move($7); }
    ;

while_loop:
    WHILE '(' while_condition ')' statement %prec WHILE
                            { $$ = WhileStatement::when($3)->do_move($5); }
    ;

for_init: statement         { $$ = $1; }
    ;

for_condition: expr         { $$ = $1; }
    ;

for_iteration: statement    { $$ = $1; }
    ;

while_condition: expr       { $$ = $1; }
    ;

switch_case:
    SWITCH '(' expr ')' '{' case_list '}'
                            { $$ = new SwitchStatement($3, $6); }
    ;

case_list:
    case_entry              { $$ = SwitchCases::one($1); }
    | case_list case_entry  { $$ = $1->add_move($2); }
    ;

case_entry:
    CASE expr ':'           { $$ = SwitchCase::when($2); }
    | CASE expr ':' statements
                            { $$ = SwitchCase::when($2, $4); }
    | DEFAULT ':' statements
                            { $$ = SwitchCase::whenDefault($3); }
    ;

flow_control:
    BREAK                   { $$ = new Break($1); }
    | CONTINUE              { $$ = new Continue($1); }
    | RETURN                { $$ = new Return($1); }
    ;

var_decl:
    VAR var_init            { $$ = VarDeclarations::one($2); }
    | var_decl ',' var_init
                            { $$ = $1->add_move($3); }
    ;

var_init:
    identifier              { $$ = new VarDeclaration($1); }
    | identifier '=' expr
                            { $$ = new VarDeclaration($1, $3); }
    ;

var_ref:
    VARREF                  { $$ = new VarRef($1); }
    ;
    
identifier:
    ID                      { $$ = new Identifier($1); }
    | UNICODE_ID            { $$ = new Identifier($1, true); }
    ;

%%

namespace yy {
    void parser::error(const std::string& msg) {
        std::cerr << "Error: " << msg << std::endl;
    }
}
