#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

namespace arglang {

    class ControlLabel : public Element {
    public:
        ControlLabel(const LexToken *source)
            : Element(source) {
            name = source->code;
        }

    public: 
        int type() const { return LABEL; }
        void accept(const IElementVisitor& visitor) override;

    private: 
        std::string name;
    };

}
