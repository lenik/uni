#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Expr.h"
#include "Argument.h"

namespace arglang {

    class Parameter : public Element {
    public:
        Parameter(Expr *expr)
            : Element(&expr->source), _expr(expr), _arg(NULL)
            {}

        Parameter(Argument *arg)
            : Element(&arg->source), _expr(NULL), _arg(arg)
            {}

        static Parameter *expr(Expr *expr) {
            return new Parameter(expr);
        }

        static Parameter *arg(Argument *arg) {
            return new Parameter(arg);
        }

    public: 
        int type() const { return PARAMETER; }
        void accept(const IElementVisitor& visitor) override;

    private:
        PExpr _expr;
        PArgument _arg;
    };

}

