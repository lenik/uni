
%option c++
%option namespace="arglang"
%option lexer="ArgLangLexer"
%option noyywrap

%top{
    #include "arglang-parser.h"
    extern LexToken token;
}

ID              [a-zA-Z_][a-zA-Z_0-9]*
UID             [\p{Alpha}_\p{Other_Letter}][\p{Alnum}_\p{Other_Letter}]*

UNICODE_WS      [ \t\p{Space}]+
WS              [ \t]+

DecimalDigit    [0-9]
DecimalDigits   [0-9][0-9_]*
OctalDigit      [0-7]
HexDigit        [0-9a-fA-F]
Binary          0[bB][01_]+
Octal           0({OctalDigit}|_)+
Decimal         0|[1-9]({DecimalDigit}|_)*
Hexadecimal     0[xX]({HexDigit}|_)+
Integer         {Decimal}|{Binary}|{Octal}|{Hexadecimal}
IntSuffix       [lL]|[uU]|[lL][uU]|[uU][lL]
INTEGER_LITERAL {Integer}{IntSuffix}?

DecimalExponent [eE][\+\-]?{DecimalDigits}
Float           {DecimalDigits}\.{DecimalDigits}?{DecimalExponent}?
FloatTypeSuffix [fFL]
ImaginarySuffix [i]
FLOAT_LITERAL   {Float}{ImaginarySuffix}?{FloatTypeSuffix}?

NSWORD          [\p{Alnum}_\p{Other_Letter}]+

SingleQ         [']
DoubleQ         ["]
BS              [\\]
OctalEsc        {BS}[0-3][0-7][0-7]|BS[0-7][0-7]|BS[0-7]
UnicodeEsc      {BS}[uU]{HexDigit}{HexDigit}{HexDigit}{HexDigit}
EscSeq          {BS}[btnfr\"\'\\]|{UnicodeEsc}|{OctalEsc}

STRING_QQ       {DoubleQ}({EscSeq}|[^\"\\\r\n])*{DoubleQ}
STRING_Q        {SingleQ}({EscSeq}|[^\"\\\r\n])*{SingleQ}

/*
 * language-specifiers
 */

%x __comment_slashstar__
%x __quoted__
%x __double_quoted__
%x __char_esc__
%x __args__

%{
    #include <stdarg.h>

    #include <string>
    #include <sstream>
    using namespace std;

    int integerLiteral(const string&);
    int floatLiteral(const string&);
    int stringLiteral(const string&, bool doubleQuoted = false);
    int newLine(const char *);
    int keyword(int, const char *);
    int op(int, const char *);
    int arg(const char *);
    int id(const char *);
    int unicodeId(const char *);
    int bareVarRef(const std::string&);
    int bracedVarRef(const std::string&);
    int other(const char *);
    int errorf(const char *fmt, ...);

    #include "lextoken.h"
    LexToken token;

    string q_buf;

    #include "arglang-parser.h"
    using namespace yy;
%}

%%

%{
    // INITIAL
%}

<__comment_slashstar__> {
.*?"*/"         { pop_state(); }
}

<__quoted__> {
\n              { pop_state(); errorf("unexpected end of a string."); }
\\              { push_state(__char_esc__); }
\'              { pop_state(); return stringLiteral(q_buf); }
.               { q_buf += text()[0]; }
}

<__double_quoted__> {
\n              { pop_state(); errorf("unexpected end of a string."); }
\\              { push_state(__char_esc__); }
\"              { pop_state(); return stringLiteral(q_buf, true); }
.               { q_buf += text()[0]; }
}

<__char_esc__> {
a               { q_buf += '\a'; pop_state(); }
b               { q_buf += '\b'; pop_state(); }
n               { q_buf += '\n'; pop_state(); }
r               { q_buf += '\r'; pop_state(); }
0               { q_buf += (char) 0; pop_state(); }
\\              { q_buf += '\\'; pop_state(); }
"\""            { q_buf += '\"'; pop_state(); }
[']             { q_buf += '\''; pop_state(); }
.               { q_buf += text()[0]; pop_state(); }
}

\r*\n               { return newLine(text()); }
{UNICODE_WS}        { /* eats up */ }

"#".*               /* eat up */
"//".*              /* eat up */
"/*"                { push_state(__comment_slashstar__); }

{FLOAT_LITERAL}     { return floatLiteral(text()); }
{INTEGER_LITERAL}   { return integerLiteral(text()); }

\'                  { q_buf.clear(); push_state(__quoted__); }
\"                  { q_buf.clear(); push_state(__double_quoted__); }

\\{ID}              { push_state(__args__); }

<__args__> {
[^ \t\p{Space}]+    { return arg(text()); }
\r*\n               { pop_state(); }
}

if                  { return keyword(parser::token::IF, text()); }
else                { return keyword(parser::token::ELSE, text()); }
for                 { return keyword(parser::token::FOR, text()); }
break               { return keyword(parser::token::BREAK, text()); }
continue            { return keyword(parser::token::CONTINUE, text()); }
return              { return keyword(parser::token::RETURN, text()); }
switch              { return keyword(parser::token::SWITCH, text()); }
case                { return keyword(parser::token::CASE, text()); }
default             { return keyword(parser::token::DEFAULT, text()); }
var                 { return keyword(parser::token::VAR, text()); }
class               { return keyword(parser::token::CLASS, text()); }
static              { return keyword(parser::token::STATIC, text()); }
do                  { return keyword(parser::token::DO, text()); }
in                  { return keyword(parser::token::IN, text()); }
eval                { return keyword(parser::token::EVAL, text()); }

{ID}                { return id(text()); }
{UID}               { return unicodeId(text()); }

"$"{ID}             { return bareVarRef(text()); }
"${".*?"}"          { return bracedVarRef(text()); }

"=="                { return op(parser::token::EQ, text()); }
"!="                { return op(parser::token::NEQ, text()); }
">="                { return op(parser::token::GEQ, text()); }
"<="                { return op(parser::token::LEQ, text()); }

">"                 { return op('>', text()); }
">>"                { return op(parser::token::GTGT, text()); }
[1-9][0-9]*">"      { return op(parser::token::N_GT, text()); }
[1-9][0-9]*">>"     { return op(parser::token::N_GTGT, text()); }
"<"                 { return op('<', text()); }
[1-9][0-9]*"<"      { return op(parser::token::N_LT, text()); }
"|"                 { return op('|', text()); }
[1-9][0-9]*"|"      { return op(parser::token::N_PIPE, text()); }

.                   { return other(text()); }

%%

int integerLiteral(const string& text) {
    token.code = text;
    token.integral = stol(text);
    return parser::token::INTEGER;
}

int floatLiteral(const string& text) {
    token.code = text;
    token.decimal = stof(text);
    return parser::token::FLOAT;
}

int stringLiteral(const string& text, bool doubleQuoted) {
    token.code = text;
    token.string = text;
    return parser::token::STRING;
}

int newLine(const char *text) {
    token.code = text;
    return parser::token::EOL;
}

int keyword(int id, const char *text) {
    token.code = text;
    return id;
}

int op(int id, const char *text) {
    token.code = text;
    return id;
}

int arg(const char *text) {
    token.code = text;
    token.string = text;
    return parser::token::ARG;
}

int id(const char *text) {
    token.code = text;
    token.string = text;
    return parser::token::ID;
}

int unicodeId(const char *text) {
    token.code = text;
    token.string = text;
    return parser::token::UNICODE_ID;
}

int varRef(const std::string& code, const std::string& text) {
    token.code = code;
    token.string = text;
    return parser::token::VARREF;
}

int bareVarRef(const std::string &code) {
    return varRef(code, code.substr(1));
}

int bracedVarRef(const std::string& code) {
    int n = code.length();
    std::string name = code.substr(1, n - 2);
    return varRef(code, name);
}

int other(const char *text) {
    token.code = text;
    return text[0];
}

int errorf(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    int n = vfprintf(stderr, fmt, ap);
    va_end(ap);
    return n;
}
