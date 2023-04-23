#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Statement.h"
#include "Arguments.h"

namespace arglang {

    class CmdLine : public Statement {
    public:

        CmdLine(const LexToken *command)
            : command(command) {
            arguments = PArguments(new Arguments);
        }

        CmdLine(const LexToken *command, Arguments *arguments)
            : command(command), arguments(arguments) {
        }

    public: 
        int type() const { return CMDLINE; }
        void accept(const IElementVisitor& visitor) override;
        
        CmdLine *add_move(Argument *arg) {
            arguments->add_move(arg);
            return this;
        }

    private: 
        PLexToken command;
        PArguments arguments;
    };

}

