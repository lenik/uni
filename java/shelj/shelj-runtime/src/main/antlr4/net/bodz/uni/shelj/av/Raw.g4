grammar Raw;

// WS : [ \t\r\n]+ -> skip ; 
UNICODE_WS: [ \t\r\n\p{White_Space}]+ -> skip ; 
SLCOMMENT: '#' ~[\r\n]* '\r'? ('\n' | EOF) -> skip ;

ID: (Alpha | '_') (Alnum | '_')*;
UID: UAlpha UAlnum*;

ARG: (Alnum | SymLetter)+;
UARG: (UAlnum | SymLetter)+;

fragment Alpha: [A-Za-z];
fragment Alnum: [A-Za-z0-9];
fragment UAlpha: [\p{Alpha}\p{General_Category=Other_Letter}];
fragment UAlnum: [\p{Alnum}\p{General_Category=Other_Letter}];
fragment SymLetter: [_%^=+:,./~\-];

STRING_LITERAL : '"' (~('"' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '"';

parse: args ';';

args: arg
	| args arg 
	| args ',' arg
	; 

arg: id | uid | word | uword;
id: ID;
uid: UID;
word: ARG;
uword: UARG;
