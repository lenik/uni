#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

namespace arglang {

    enum BasicType {
        CHAR,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOL,
        STRING,
        VARIANT,
    };

    class LiteralValue;
    class VarRef;
    class Identifier;
    class FunctionCall;
    class Enclosed;

    class Expr : public Element {
    public:
        Expr() {}
        Expr(const LexToken *source)
            : Element(source) {}

        // virtual BasicType type() const = 0;
    };

}

