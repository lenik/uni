grammar Arguments;

ESC_CMD: '\\' ~[\r\n]* '\r'? ('\n'|EOF);

IF: 'if';
ELSE: 'else';
FOR: 'for';
BREAK: 'break';
CONTINUE: 'continue';
RETURN: 'return';
SWITCH: 'switch';
CASE: 'case';
DEFAULT: 'default';

VAR: 'var';
CLASS: 'class';
STATIC: 'static';

KEYWORD: IF | ELSE | FOR | BREAK | CONTINUE | RETURN | SWITCH | CASE | DEFAULT 
	| VAR | CLASS | STATIC;

ID : [a-zA-Z_] [a-zA-Z_0-9]*;
UID : [\p{Alpha}_\p{General_Category=Other_Letter}] [\p{Alnum}_\p{General_Category=Other_Letter}]*;

// WS : [ \t\r\n]+ -> skip ; 
UNICODE_WS : [ \t\r\n\p{White_Space}]+ -> skip ; 
SLCOMMENT : '#' ~[\r\n]* '\r'? '\n' -> skip ;

INTEGER_LITERAL: Integer IntSuffix?;
FLOAT_LITERAL: Float ImaginarySuffix? FloatTypeSuffix?;
fragment Float: DecimalDigits ('.' DecimalDigits)? DecimalExponent?;
fragment DecimalExponent : 'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' DecimalDigits;
fragment DecimalDigits   : ('0'..'9')('0'..'9'|'_')* ;
fragment FloatTypeSuffix : 'f' | 'F' | 'L';
fragment ImaginarySuffix : 'i';
fragment IntSuffix       : 'L'|'u'|'U'|'Lu'|'LU'|'uL'|'UL' ;
fragment Integer         : Decimal| Binary| Octal| Hexadecimal ;
fragment Decimal         : '0' | '1'..'9' (DecimalDigit | '_')* ;
fragment Binary          : ('0b' | '0B') ('0' | '1' | '_')+;
fragment Octal           : '0' (OctalDigit | '_')+ ;
fragment Hexadecimal     : ('0x' | '0X') (HexDigit | '_')+;  
fragment DecimalDigit    : '0'..'9' ;
fragment OctalDigit      : '0'..'7' ;
fragment HexDigit        : ('0'..'9'|'a'..'f'|'A'..'F') ;

//NSWORD: ~([ \t\r\n\p{White_Space}] | '\'') +;
NSWORD: [\p{Alnum}_\p{General_Category=Other_Letter}] +;

fragment DoubleQ: '"';
fragment SingleQ: '\'';
fragment BS: '\\'; 
fragment EscSeq: BS ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\') | UnicodeEsc | OctalEsc;
fragment OctalEsc: BS ('0'..'3') ('0'..'7') ('0'..'7') | BS ('0'..'7') ('0'..'7') | BS ('0'..'7');
fragment UnicodeEsc: BS 'u' HexDigit HexDigit HexDigit HexDigit;

STRING_QQ: DoubleQ (EscSeq | ~('"' | '\\' | '\r' | '\n'))* DoubleQ;
STRING_Q: SingleQ (EscSeq | ~('\'' | '\\' | '\r' | '\n'))* SingleQ;

identifier: ID | UID;

number:
	INTEGER_LITERAL
	| FLOAT_LITERAL
	;

string: 
	STRING_QQ | STRING_Q
	;

literal_value: number | string;

statement:
	ID '=' expr statement
	| ESC_CMD
    | cmd_call
    | fn_call
    | identifier op=('=' | ':=') expr
    | if_statement
    | for_statement
    | switch_statement
    | branch_statement
    | '{' statements '}'
 	;

branch_statement: 
	'return'
	| 'break'
	| 'continue'
	;
	
cmd_call:
	cmd=ID args?
	;
	
arg: identifier | literal_value | NSWORD | KEYWORD;

fn_call:
	ID '(' params? ')'
	;
	
param: 
	identifier | literal_value;

expr:
	identifier
	| literal_value
    | identifier '=' expr
	| fn_call
	
	// unary operator
    | op=('!' | '~') expr
    
    // binary operator
    | expr op='^^' expr
    | expr op=('*' | '/') expr
    | expr op=('+' | '-') expr
    | expr op='^' expr
    | expr op=('&' | '|') expr
    | expr op=('&&' | '||') expr
    | expr op=('==' | '!=' | '>' | '<' | '>=' | '<=') expr
    
    // tenary operator
	| expr op='?' expr ':' expr
	
	// slicing
    | expr op='[' index=expr ']'
    | expr op='[' start=expr ':' end=expr ']'
    | expr op='[' start=expr ':' ']'
    | expr op='[' ':' end=expr ']'
	;

if_statement:
	IF '(' cond=expr ')' statement else_part?
	;

else_part:
	ELSE statement
	;

for_statement:
	for_loop
	| for_each
	;

for_loop:
	FOR '(' init=expr ';' check=expr ';' step=expr ')' statement;

for_each:
	FOR '(' identifier ':' coll=expr ')' statement
	| FOR '(' VAR identifier ':' coll=expr ')' statement
	;

switch_statement:
	SWITCH '(' expr ')' '{' switch_cases switch_default? '}'
	;

switch_cases:
	switch_case
	| switch_cases switch_case
	;
	
switch_case:
	CASE expr ':' statements;

switch_default:
	DEFAULT ':' statements;
	
// make up.

script: statements;

statements:
	statement
	| statements ';' statement;

args: 
	arg
	| args arg;
	
params:
	param
	| params ',' param;
