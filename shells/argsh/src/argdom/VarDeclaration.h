#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Statement.h"
#include "Identifier.h"
#include "Expr.h"

namespace arglang {

    class VarDeclaration : public Statement {
    public:
        VarDeclaration(Identifier *identifier)
            : identifier(identifier)
            {}

        VarDeclaration(Identifier *identifier, Expr *init)
            : identifier(identifier), init(init)
            {}

    public: 
        int type() const { return VAR_DECL; }
        void accept(const IElementVisitor& visitor) override;

    private: 
        PIdentifier identifier;
        PExpr init;
    };

}
