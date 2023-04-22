#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "RedirectFragment.h"

namespace arglang {

    class RedirectFragments : public Element {
    public:
        RedirectFragments() {}

        static RedirectFragments *one(RedirectFragment *statement) {
            RedirectFragments *o = new RedirectFragments();
            o->fragments.push_back(PRedirectFragment(statement));
            return o;
        }

    public: 
        int type() const { return R_FRAGMENTS; }
        void accept(const IElementVisitor& visitor) override;

        RedirectFragments *add_move(RedirectFragment *statement) {
            fragments.push_back(PRedirectFragment(statement));
            return this;
        }

    private:
        std::vector<PRedirectFragment> fragments;
    };                                              


}

