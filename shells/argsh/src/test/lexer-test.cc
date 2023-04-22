#include <iostream>
using namespace std;

#include "arglang-lexer.h"
using namespace arglang;

using namespace yy;

const char *tokname(int tok) {
    switch (tok) {
    case parser::token::YYEOF:      return "YYEOF"; 
    case parser::token::INTEGER:    return "INTEGER"; 
    case parser::token::FLOAT:      return "FLOAT"; 
    case parser::token::STRING:     return "STRING"; 
    case parser::token::ARG:        return "ARG"; 
    case parser::token::EOL:        return "EOL"; 
    case parser::token::IF:         return "IF"; 
    case parser::token::ELSE:       return "ELSE"; 
    case parser::token::FOR:        return "FOR"; 
    case parser::token::BREAK:      return "BREAK"; 
    case parser::token::CONTINUE:   return "CONTINUE"; 
    case parser::token::RETURN:     return "RETURN"; 
    case parser::token::SWITCH:     return "SWITCH"; 
    case parser::token::CASE:       return "CASE"; 
    case parser::token::DEFAULT:    return "DEFAULT"; 
    case parser::token::VAR:        return "VAR"; 
    case parser::token::CLASS:      return "CLASS"; 
    case parser::token::STATIC:     return "STATIC"; 
    case parser::token::THEN:       return "THEN"; 
    }
    return NULL;
}

int main() {
    ArgLangLexer lexer;

    yy::parser::semantic_type lval;
    int id;
    int cnt = 0;
    while ((id = lexer.lex(lval)) > 0) {
        if (++cnt % 8 == 0)
            cout << endl;
        if (cnt != 0) cout << ", ";
    
        const char *nam = tokname(id);
        if (nam) cout << nam; else cout << id;
        cout << ": ";

        switch (id) {
        case parser::token::INTEGER:
            cout << "int(" << token.integral << ")";
            break;
        case parser::token::FLOAT:
            cout << "float(" << token.decimal << ")";
            break;
        case parser::token::STRING:
            cout << "string(" 
                << token.string
                << ")";
            break;
        default:
            cout << "'" << token.code << "'";
            ;
        }
        token.clear();
    }
    cout << endl;
    cout << "done" << endl;
}