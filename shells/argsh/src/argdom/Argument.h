#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Element.h"
#include "LiteralValue.h"
#include "VarRef.h"
#include "Identifier.h"
#include "RawChars.h"

namespace arglang {

    class Argument : public Element {
    public:
        Argument() {}

        static Argument *literal(LiteralValue *value) {
            return (new Argument)->add_move(value);
        }

        static Argument *varRef(VarRef *varRef) {
            return (new Argument)->add_move(varRef);
        }
        
        static Argument *identifier(Identifier *identifier) {
            return (new Argument)->add_move(identifier);
        }

        static Argument *rawChars(LexToken *source) {
            RawChars *rc = new RawChars(source);
            return (new Argument)->add_move(rc);
        }

    public: 
        int type() const { return ARGUMENT; }
        void accept(const IElementVisitor& visitor) override;

        Argument *add_move(Element *element) {
            pieces.push_back(PElement(element));
            return this;
        }

        std::string text() {
            std::string buf;
            for (auto it = pieces.begin(); it < pieces.end(); it++) {
                auto piece = (*it)->source.code;
                buf += piece;
            }
            return buf;
        }

    public: 
        std::vector<PElement> pieces;
    };

}

