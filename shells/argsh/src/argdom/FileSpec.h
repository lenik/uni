#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Element.h"

namespace arglang {

    class FileSpec : public Element {
    public:
        explicit FileSpec(const LexToken *source)
            : Element(source) {
        }

    public: 
        int type() const { return FILE_SPEC; }
        void accept(const IElementVisitor& visitor) override;
    };

}

