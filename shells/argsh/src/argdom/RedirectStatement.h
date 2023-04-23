#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Statement.h"
#include "RedirectFragments.h"
#include "RedirectFragment.h"

namespace arglang {

    class RedirectStatement : public Statement {
    public:
        RedirectStatement(Statement *statement, RedirectFragments *fragments = NULL)
            : statement(statement) {
            if (fragments == NULL)
                fragments = new RedirectFragments;
            this->fragments = PRedirectFragments(fragments);
        }

    public: 
        int type() const { return REDIRECT; }
        void accept(const IElementVisitor& visitor) override;

        RedirectStatement *add_move(RedirectFragment *fragment) {
            fragments->add_move(fragment);
            return this;
        }

    private: 
        PStatement statement;
        PRedirectFragments fragments;
    };

}
