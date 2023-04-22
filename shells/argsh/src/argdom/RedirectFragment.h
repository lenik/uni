#pragma once

#include <string>
#include <vector>

#include "../lextoken.h"
#include "dom-base.h"

#include "FileSpec.h"
#include "Statement.h"

namespace arglang {

    enum Direction {
        OUTPUT,
        APPEND,
        INPUT,
        PIPE
    };

    class RedirectFragment : public Element {
    public:
        RedirectFragment(Direction dir, FileSpec *fileSpec, int fd = 0) 
            : dir(dir), fileSpec(fileSpec), statement(NULL), fd(fd) {
        }
        RedirectFragment(Direction dir, Statement *statement, int fd = 0) 
            : dir(dir), fileSpec(NULL), statement(NULL), fd(fd) {
        }

        static RedirectFragment *outputTo(FileSpec *fileSpec, int fd = 0) {
            return new RedirectFragment(OUTPUT, fileSpec, fd);
        }
        static RedirectFragment *appendTo(FileSpec *fileSpec, int fd = 0) {
            return new RedirectFragment(APPEND, fileSpec, fd);
        }
        static RedirectFragment *inputFrom(FileSpec *fileSpec, int fd = 0) {
            return new RedirectFragment(INPUT, fileSpec, fd);
        }
        static RedirectFragment *pipeTo(Statement *statement, int fd = 0) {
            return new RedirectFragment(PIPE, statement, fd);
        }

    public: 
        int type() const { return R_FRAGMENT; }
        void accept(const IElementVisitor& visitor) override;

    private: 
        int fd;
        Direction dir;
        PFileSpec fileSpec;
        PStatement statement;
    };

}
