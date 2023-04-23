#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Expr.h"
#include "SwitchCase.h"
#include "SwitchCases.h"

namespace arglang {

    class SwitchStatement : public Statement {
    public:
        SwitchStatement(Expr *expr)
            : expr(expr)
            {}
        SwitchStatement(Expr *expr, SwitchCases *cases)
            : expr(expr), cases(cases)
            {}

    public: 
        int type() const { return SWITCH; }
        void accept(const IElementVisitor& visitor) override;

        SwitchStatement *add_move(SwitchCase *_case) {
            cases->add_move(_case);
            return this;
        }

    private: 
        PExpr expr;
        PSwitchCases cases;
    };

}

