#pragma once

#include <string>
#include <vector>
#include <memory>

#include "../lextoken.h"

namespace arglang {

    class IElementVisitor;

    enum ElementType {
        ARGUMENT        = 100,
        ARGUMENTS,
        BINARY_EXPR     = 200,
        BREAK,
        SWITCH_CASE            = 300,
        SWITCH_CASES,
        C_FOR,
        CMDLINE,
        CONTINUE,
        EVAL            = 500,
        FILE_SPEC       = 600,
        FNCALL,
        FOR_EACH,
        ID              = 900,
        IF,
        LABEL,
        NOP             = 1400,
        PARAMETER       = 1600,
        PARAMETERS,
        PROGRAM,
        RAWCHARS        = 1800,
        REDIRECT,
        RETURN,
        R_FRAGMENT,
        R_FRAGMENTS,
        STATEMENT       = 1900,
        STATEMENTS,
        SWITCH,
        STRING_LITERAL,
        INT_LITERAL,
        FLOAT_LITERAL,
        VAR_DECL        = 2200,
        VAR_DECLS,
        VARREF,
        WHILE           = 2300,
    };
    
    class Element {
    public:
        Element() {}
        Element(const LexToken *source)
            : source(*source)
            {}

    public:
        virtual int type() const = 0;
        virtual void accept(const IElementVisitor& visitor) = 0;

    public:
        LexToken source;
    };

    class IElementVisitor {

        virtual void element(Element *element) = 0;
        virtual void enter(Element *childElement) = 0;
        virtual void leave(Element *childElement) {
            std::ignore = childElement;
        }

    };

}
