#include "Statement.h"

#include "RedirectFragments.h"
#include "RedirectStatement.h"

namespace arglang {

    void Statement::accept(const IElementVisitor& visitor) {

    }

    RedirectStatement *Statement::redirect(RedirectFragments *fragments) {
        RedirectStatement *rs = new RedirectStatement(this, fragments);
        return rs;
    }

}