#include "BinaryExpr.h"

namespace arglang {

    BinaryExpr *BinaryExpr::add_move(Expr *left, Expr *right) {
        return new BinaryExpr(ADD, left, right);
    }

    BinaryExpr *BinaryExpr::subtract(Expr *left, Expr *right) {
        return new BinaryExpr(SUBTRACT, left, right);
    }

    BinaryExpr *BinaryExpr::multiply(Expr *left, Expr *right) {
        return new BinaryExpr(MULTIPLY, left, right);
    }

    BinaryExpr *BinaryExpr::divide(Expr *left, Expr *right) {
        return new BinaryExpr(DIVIDE, left, right);
    }



    BinaryExpr *BinaryExpr::_and(Expr *left, Expr *right) {
        return new BinaryExpr(AND, left, right);
    }

    BinaryExpr *BinaryExpr::_or(Expr *left, Expr *right) {
        return new BinaryExpr(OR, left, right);
    }

    BinaryExpr *BinaryExpr::_xor(Expr *left, Expr *right) {
        return new BinaryExpr(XOR, left, right);
    }



    BinaryExpr *BinaryExpr::lessThan(Expr *left, Expr *right) {
        return new BinaryExpr(LESS, left, right);
    }

    BinaryExpr *BinaryExpr::greaterThan(Expr *left, Expr *right) {
        return new BinaryExpr(GREATER, left, right);
    }

    BinaryExpr *BinaryExpr::lessOrEquals(Expr *left, Expr *right) {
        return new BinaryExpr(LESS_EQ, left, right);
    }

    BinaryExpr *BinaryExpr::greaterOrEquals(Expr *left, Expr *right) {
        return new BinaryExpr(GREATER_EQ, left, right);
    }

    BinaryExpr *BinaryExpr::equals(Expr *left, Expr *right) {
        return new BinaryExpr(EQ, left, right);
    }

    BinaryExpr *BinaryExpr::notEquals(Expr *left, Expr *right) {
        return new BinaryExpr(NEQ, left, right);
    }



    BinaryExpr *BinaryExpr::lshift(Expr *left, Expr *right) {
        return new BinaryExpr(LSHIFT, left, right);
    }

    BinaryExpr *BinaryExpr::rshift(Expr *left, Expr *right) {
        return new BinaryExpr(RSHIFT, left, right);
    }



    BinaryExpr *BinaryExpr::assign(Expr *left, Expr *right) {
        return new BinaryExpr(ASSIGN, left, right);
    }



    void BinaryExpr::accept(const IElementVisitor& visitor) {
    }

}