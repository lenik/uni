#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "SwitchCase.h"

namespace arglang {

    class SwitchCases : public SwitchCase {
    public:
        
        static SwitchCases *one(SwitchCase *statement) {
            SwitchCases *o = new SwitchCases();
            o->cases.push_back(PSwitchCase(statement));
            return o;
        }

    public: 
        int type() const { return SWITCH_CASES; }
        void accept(const IElementVisitor& visitor) override;

        SwitchCases *add_move(SwitchCase *statement) {
            cases.push_back(PSwitchCase(statement));
            return this;
        }

    private:
        std::vector<PSwitchCase> cases;
    };                                              

}
