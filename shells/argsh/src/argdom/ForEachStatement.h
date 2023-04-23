#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "ForStatement.h"
#include "Identifier.h"
#include "Expr.h"
#include "Statements.h"

namespace arglang {

    class ForEachStatement : public ForStatement {
    public:
        ForEachStatement(Identifier *identifier, Expr *expr)
            : identifier(identifier), expr(expr)
            {}

    public: 
        int type() const { return FOR_EACH; }
        void accept(const IElementVisitor& visitor) override;
        
        ForStatement *add_move(Statement *statement) {
            statements->add_move(statement);
            return this;
        }

        ForStatement *do_move(Statement *statement) {
            statements->add_move(statement);
            return this;
        }

    private:
        PIdentifier identifier;
        PExpr expr;
        PStatements statements;
    };

}
