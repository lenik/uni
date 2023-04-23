#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Statement.h"
#include "VarDeclaration.h"

namespace arglang {

    class VarDeclarations : public Statement {
    public:
        static VarDeclarations *one(VarDeclaration *varDecl) {
            VarDeclarations *o = new VarDeclarations();
            o->add_move(varDecl);
            return o;
        }

    public: 
        int type() const { return VAR_DECLS; }
        void accept(const IElementVisitor& visitor) override;

        VarDeclarations *add_move(VarDeclaration *varDecl) {
            varDecls.push_back(PVarDeclaration(varDecl));
            return this;
        }

    private: 
        std::vector<PVarDeclaration> varDecls;
    };

}

