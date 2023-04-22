#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Statement.h"
#include "Expr.h"

namespace arglang {

    class EvalStatement : public Statement {
    public:
        EvalStatement(Expr *expr)
            : expr(expr)
        {}

    public: 
        int type() const { return EVAL; }
        void accept(const IElementVisitor& visitor) override;

    private: 
        PExpr expr;
    };

}

