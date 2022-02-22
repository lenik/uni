grammar Arguments;
 
ID : [a-zA-Z] [a-zA-Z0-9]*;
UID : [\p{Alpha}\p{General_Category=Other_Letter}] [\p{Alnum}\p{General_Category=Other_Letter}]*;

// WS : [ \t\r\n]+ -> skip ; 
UNICODE_WS : [ \t\r\n\p{White_Space}]+ -> skip ; 
SLCOMMENT : '#' ~[\r\n]* '\r'? '\n' -> skip ;

NSWORD: ~([ \t\r\n\p{White_Space}] | '\'') +;
STRING_LITERAL : '"' (~('"' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '"';

Number:
	IntegerLiteral
	| FloatLiteral
;

IntegerLiteral: Integer IntSuffix?;

FloatLiteral: Float ImaginarySuffix? FloatTypeSuffix?;

fragment Float: DecimalDigits ('.' DecimalDigits)? DecimalExponent?;
fragment DecimalExponent : 'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' DecimalDigits;
fragment DecimalDigits   : ('0'..'9'|'_')+ ;
fragment FloatTypeSuffix : 'f' | 'F' | 'L';
fragment ImaginarySuffix : 'i';
fragment IntSuffix       : 'L'|'u'|'U'|'Lu'|'LU'|'uL'|'UL' ;
fragment Integer         : Decimal| Binary| Octal| Hexadecimal ;
fragment Decimal         : '0' | '1'..'9' (DecimalDigit | '_')* ;
fragment Binary          : ('0b' | '0B') ('0' | '1' | '_')+ ;
fragment Octal           : '0' (OctalDigit | '_')+ ;
fragment Hexadecimal     : ('0x' | '0X') (HexDigit | '_')+;  
fragment DecimalDigit    : '0'..'9' ;
fragment OctalDigit      : '0'..'7' ;
fragment HexDigit        : ('0'..'9'|'a'..'f'|'A'..'F') ;

arg: NSWORD | STRING_LITERAL;

args: 
	arg
	| args arg;

script: statements;

statements:
	statement
	| statements statement;

statement:
	';'		// nop
	| ID '=' expr ';'
	| ID '=' expr statement
    | cmd_call ';'
    | fn_call ';'
    | '{' statements '}'
 	;

cmd_call:
	cmd=ID args?
	;
	
fn_call:
	ID '(' args? ')'
	;
	
expr:
	ID
	| Number
	| STRING_LITERAL
    | var_ref
    | ID '=' expr
    | expr op=('*' | '/') expr
    | expr op=('+' | '-') expr
	| fn_call
	;

var_ref:
    ID
//    | '!' ID
//    | ID '[' index=expr ']'
//    | ID '[' start=expr ':' end=expr ']'
//    | ID '[' start=expr ':' ']'
//    | ID '[' ':' end=expr ']'
	;
	
