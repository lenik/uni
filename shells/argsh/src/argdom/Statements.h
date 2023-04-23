#pragma once

#include <string>
#include <vector>

#include <lextoken.h>
#include "dom-base.h"

#include "Statement.h"

namespace arglang {

    class Statements : public Statement {
    public:
        
        static Statements *one(Statement *statement) {
            Statements *o = new Statements();
            o->seqList.push_back(PStatement(statement));
            return o;
        }

    public: 
        int type() const { return STATEMENTS; }
        void accept(const IElementVisitor& visitor) override;

        Statements *add_move(Statement *statement) {
            seqList.push_back(PStatement(statement));
            return this;
        }

        Statements *replace_move(Statement *statement) {
            Statements *seq = dynamic_cast<Statements *>(statement);
            if (seq != NULL) {
                this->source = seq->source;
                seqList = seq->seqList;
            } else {
                seqList.push_back(PStatement(statement));
            }
            return this;
        }

    private:
        std::vector<PStatement> seqList;
    };                                              

}
