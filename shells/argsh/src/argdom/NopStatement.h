#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

namespace arglang {

    class NopStatement : public Element {
    public:
        explicit NopStatement(const LexToken *source)
            : Element(source) {
        }

    public: 
        int type() const { return NOP; }
        void accept(const IElementVisitor& visitor) override;

    private: 
    };

}

