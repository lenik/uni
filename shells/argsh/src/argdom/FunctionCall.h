#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Expr.h"
#include "Identifier.h"
#include "Parameters.h"

namespace arglang {

    class FunctionCall : public Expr {
    public:
        FunctionCall(Identifier *name, Parameters *parameters)
            : name(name), parameters(parameters) {
        }

    public: 
        int type() const { return FNCALL; }
        void accept(const IElementVisitor& visitor) override;

        FunctionCall *addParameter_move(Parameter *parameter) {
            parameters->add_move(parameter);
            return this;
        }

    private: 
        PIdentifier name;
        PParameters parameters;
    };

}

