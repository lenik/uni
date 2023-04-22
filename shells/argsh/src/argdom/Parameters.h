#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "Parameter.h"

namespace arglang {

    class Parameters : public Element {
    public:
        static Parameters *one(Parameter *param) {
            Parameters *params = new Parameters();
            params->add_move(param);
            return params;
        }
        
    public: 
        int type() const { return PARAMETERS; }
        void accept(const IElementVisitor& visitor) override;
        
        Parameters *add_move(Parameter *param) {
            parameters.push_back(PParameter(param));
            return this;
        }

    private: 
        std::vector<PParameter> parameters;
    };

}