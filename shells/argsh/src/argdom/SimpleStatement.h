#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Statement.h"
#include "ControlLabel.h"
#include "Expr.h"

namespace arglang {

    class SimpleStatement : public Statement {
    public:
        SimpleStatement(const LexToken *source)
            : Statement(source)
            {}
    public:
        void accept(const IElementVisitor& visitor) override;
    };

    class Break : public SimpleStatement {
    public:
        Break(const LexToken *source)
            : SimpleStatement(source)
            {}
        Break(const LexToken *source, ControlLabel *label)
            : SimpleStatement(source), label(label)
            {}
    private:
        PControlLabel label;
    };

    class Continue : public SimpleStatement {
    public:
        Continue(const LexToken *source)
            : SimpleStatement(source)
            {}
    private:
        PControlLabel label;
    };

    class Return : public SimpleStatement {
    public:
        Return(const LexToken *source)
            : SimpleStatement(source)
            {}
        Return(const LexToken *source, Expr *value)
            : SimpleStatement(source), value(value)
            {}

    private:
        PExpr value;
    };

}

