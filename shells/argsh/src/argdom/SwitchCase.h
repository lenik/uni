#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Expr.h"
#include "Statements.h"

namespace arglang {

    class SwitchCase : public Element {
    public:
        SwitchCase() {
            statements = PStatements(new Statements);
        }

        SwitchCase(Expr *when, Statements *statements = NULL)
            : _when(when) {
            if (statements == NULL)
                statements = new Statements();
            this->statements = PStatements(statements);
        }

        static SwitchCase *when(Expr *expr, Statements *statements = NULL) {
            return new SwitchCase(expr, statements);
        }

        static SwitchCase *whenDefault(Statements *statements) {
            return new SwitchCase(NULL, statements);
        }

    public: 
        int type() const { return SWITCH_CASE; }
        void accept(const IElementVisitor& visitor) override;

        SwitchCase *add_move(Statement *statement) {
            statements->add_move(statement);
            return this;
        }

        SwitchCase *replace_move(Statements *statements) {
            this->statements = PStatements(statements);
            return this;
        }

    private: 
        PExpr _when;
        PStatements statements;
    };

}
