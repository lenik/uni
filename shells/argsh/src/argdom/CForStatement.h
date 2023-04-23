#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "ForStatement.h"
#include "Expr.h"
#include "Statements.h"

namespace arglang {

    class CForStatement : public ForStatement {
    public:
        CForStatement(Statement *init, Expr *condition, Statement *step)
            : init(init), condition(condition), step(step)
            {}

    public: 
        int type() const { return C_FOR; }
        void accept(const IElementVisitor& visitor) override;

        CForStatement *add_move(Statement *statement) {
            statements->add_move(statement);
            return this;
        }

        CForStatement *do_move(Statement *statement) {
            statements->replace_move(statement);
            return this;
        }

    private:
        PStatement init;
        PExpr condition;
        PStatement step;
        PStatements statements;
    };

}

