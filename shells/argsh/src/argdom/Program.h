#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Statements.h"
#include "Statement.h"

namespace arglang {

    class Program : public Element {
    public:
        explicit Program(Statements *statements)
            : _statements(statements) {
        }

    public: 
        int type() const { return PROGRAM; }
        void accept(const IElementVisitor& visitor) override;

        Program *add_move(Statement *statement);
        
    private: 
        PStatements _statements;
    };

}

