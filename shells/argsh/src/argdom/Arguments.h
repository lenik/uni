#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Argument.h"

namespace arglang {

    class Arguments : public Element {
    public:
        Arguments() {}

        static Arguments *one(Argument *arg) {
            Arguments *o = new Arguments();
            o->add_move(arg);
            return o;
        }

    public: 
        int type() const { return ARGUMENTS; }
        void accept(const IElementVisitor& visitor) override;

        Arguments *add_move(Argument *arg) {
            args.push_back(PArgument(arg));
            return this;
        }
        
    private: 
        std::vector<PArgument> args;
    };

}

