#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Expr.h"

namespace arglang {

    class LiteralValue : public Expr {
    public:
        LiteralValue(const LexToken *source)
            : Expr(source)
            {}
    };

    class IntegerLiteral : public LiteralValue {
    public:
        IntegerLiteral(const LexToken *source)
            : LiteralValue(source) {
            value = source->integral;
        }

    public: 
        int type() const { return INT_LITERAL; }
        void accept(const IElementVisitor& visitor) override;

    private:
        long value;
    };

    class FloatLiteral : public LiteralValue {
    public:
        FloatLiteral(const LexToken *source)
            : LiteralValue(source) {
            value = source->decimal;
        }

    public: 
        int type() const { return FLOAT_LITERAL; }
        void accept(const IElementVisitor& visitor) override;

    private:
        double value;
    };

    class StringLiteral : public LiteralValue {
    public:
        StringLiteral(const LexToken *source)
            : LiteralValue(source) {
            value = source->string;
        }

    public: 
        int type() const { return STRING_LITERAL; }
        void accept(const IElementVisitor& visitor) override;

    private:
        std::string value;
    };

}