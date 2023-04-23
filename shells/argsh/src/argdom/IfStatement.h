#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Expr.h"
#include "Statement.h"
#include "Statements.h"

namespace arglang {

    class IfStatement : public Statement {
    public:
        IfStatement() {
            thenList = PStatements(new Statements);
            elseList = PStatements(new Statements);
        }
        IfStatement(Expr *when, Statement *then)
            : _when(when) {
            thenList = PStatements(new Statements);
            elseList = PStatements(new Statements);
            if (then != NULL)
                thenList->replace_move(then);
        }

    public: 
        int type() const { return IF; }
        void accept(const IElementVisitor& visitor) override;
        
        IfStatement *when_move(Expr *_when) {
            this->_when = PExpr(_when);
            return this;
        }

        IfStatement *addThen_move(Statement *statement) {
            thenList->add_move(statement);
            return this;
        }

        IfStatement *addElse_move(Statement *statement) {
            elseList->add_move(statement);
            return this;
        }

        IfStatement *then_move(Statement *statement) {
            thenList->replace_move(statement);
            return this;
        }

        IfStatement *else_move(Statement *statement) {
            elseList->replace_move(statement);
            return this;
        }
        
    private: 
        PExpr _when;
        PStatements thenList;
        PStatements elseList;
    };

}

