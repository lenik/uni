#include "ForStatement.h"
#include "CForStatement.h"
#include "ForEachStatement.h"

namespace arglang {

    CForStatement *ForStatement::c(Statement *init, Expr *condition, Statement *step) {
        return new CForStatement(init, condition, step);
    }

    ForEachStatement *ForStatement::each(Identifier *identifier, Expr *collection) {
        return new ForEachStatement(identifier, collection);
    }
    
}
