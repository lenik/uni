#include "arglang-lexer.h"
using namespace arglang;


int main() {

    ArgLangLexer lexer;

    yy::parser::semantic_type lval;
    lexer.lex(lval);
}