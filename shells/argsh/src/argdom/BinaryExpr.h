#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Expr.h"

namespace arglang {

    enum Opcode {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        AND,
        OR,
        XOR,
        LESS,
        GREATER,
        LESS_EQ,
        GREATER_EQ,
        EQ,
        NEQ,
        LSHIFT,
        RSHIFT,
        ASSIGN,
    };

    class BinaryExpr : public Expr {
    public:
        BinaryExpr(Opcode opcode, Expr *left, Expr *right)
            : opcode(opcode), left(left), right(right) 
            {}

        static BinaryExpr *add_move(Expr *left, Expr *right);
        static BinaryExpr *subtract(Expr *left, Expr *right);
        static BinaryExpr *multiply(Expr *left, Expr *right);
        static BinaryExpr *divide(Expr *left, Expr *right);

        static BinaryExpr *_and(Expr *left, Expr *right);
        static BinaryExpr *_or(Expr *left, Expr *right);
        static BinaryExpr *_xor(Expr *left, Expr *right);

        static BinaryExpr *lessThan(Expr *left, Expr *right);
        static BinaryExpr *greaterThan(Expr *left, Expr *right);
        static BinaryExpr *lessOrEquals(Expr *left, Expr *right);
        static BinaryExpr *greaterOrEquals(Expr *left, Expr *right);
        static BinaryExpr *equals(Expr *left, Expr *right);
        static BinaryExpr *notEquals(Expr *left, Expr *right);
        
        static BinaryExpr *lshift(Expr *left, Expr *right);
        static BinaryExpr *rshift(Expr *left, Expr *right);
        
        static BinaryExpr *assign(Expr *left, Expr *right);
        
    public:
        int type() const { return BINARY_EXPR; }
        void accept(const IElementVisitor& visitor) override;
    
    private:
        Opcode opcode;
        PExpr left;
        PExpr right;
    };

}

