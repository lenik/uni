#include "Program.h"

namespace arglang {

    void Program::accept(const IElementVisitor& visitor) {

    }

    Program *Program::add_move(Statement *statement) {
        this->_statements->add_move(statement);
        return this;
    }

}