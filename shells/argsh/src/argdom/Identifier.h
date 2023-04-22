#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Element.h"

namespace arglang {

    class Identifier : public Element {
    public:
        Identifier(const LexToken *source, bool unicode = false)
            : Element(source), unicode(unicode) {
            name = source->string;
        }

    public: 
        int type() const { return ID; }
        void accept(const IElementVisitor& visitor) override;
        
    public: 
        std::string name;
        bool unicode;
    };

}

