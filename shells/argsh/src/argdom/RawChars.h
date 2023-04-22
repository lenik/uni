#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

namespace arglang {

    class RawChars : public Element {
    public:
        explicit RawChars(const LexToken *source)
            : Element(source) {
        }

    public: 
        int type() const { return RAWCHARS; }
        void accept(const IElementVisitor& visitor) override;
    };

}

