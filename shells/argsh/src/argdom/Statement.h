#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

namespace arglang {

    class RedirectStatement;
    class RedirectFragments;
    
    class Statement : public Element {
    public:
        Statement() {}
        Statement(const LexToken *source)
            : Element(source)
            {}
            
    public:
        int type() const { return STATEMENT; }
        void accept(const IElementVisitor& visitor) override;

        RedirectStatement *redirect(RedirectFragments *fragments);
        
    };

}

