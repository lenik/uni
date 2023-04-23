#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Statement.h"
#include "Expr.h"
#include "Identifier.h"

namespace arglang {

    class CForStatement;
    class ForEachStatement;
    
    class ForStatement : public Statement {
    public:
        static CForStatement *c(Statement *init, Expr *condition, Statement *iteration);
        static ForEachStatement *each(Identifier *identifier, Expr *expr);
    };

}