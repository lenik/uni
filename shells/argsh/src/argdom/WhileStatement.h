#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Expr.h"
#include "Statements.h"

namespace arglang {

    class WhileStatement : public Statement {
    public:
        WhileStatement(Expr *condition)
            : condition(condition)
            {}

        static WhileStatement *when(Expr *condition) {
            WhileStatement *o = new WhileStatement(condition);
            return o;
        }

    public: 
        int type() const { return WHILE; }
        void accept(const IElementVisitor& visitor) override;

        WhileStatement *add_move(Statement *statement) {
            statements->add_move(statement);
            return this;
        }

        WhileStatement *do_move(Statement *statement) {
            statements->replace_move(statement);
            return this;
        }

    private: 
        PExpr condition;
        PStatements statements;
    };

}
