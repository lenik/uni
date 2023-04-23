#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Expr.h"
#include "Identifier.h"

namespace arglang {

    class VarRef : public Expr {
    public:
        explicit VarRef(const LexToken *source)
            : Expr(source) {
            text = source->string;
        }

        VarRef(Identifier *identifier) 
            : Expr(&identifier->source) {
            text = identifier->name;
        }

    public: 
        int type() const { return VARREF; }
        void accept(const IElementVisitor& visitor) override;

    private: 
        std::string text;
    };

}

