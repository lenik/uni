#pragma once

#include <string>
#include <vector>
#include <memory>

struct LexValue {
    char ch;
    std::string string;
    long integral;
    double decimal;

    LexValue() {}
    LexValue(const LexValue& copy):
        ch(copy.ch), 
        string(copy.string),
        integral(copy.integral),
        decimal(copy.decimal)
        {}

    void clear() {
        ch = 0;
        integral = 0;
        decimal = 0;
        string.clear();
    }
};

struct LexToken : public LexValue {
    std::string code;
    int line;
    int column;

    LexToken() {}
    LexToken(const LexToken& copy):
        LexValue(copy),
        code(copy.code),
        line(copy.line),
        column(copy.column)
        {}
    
    inline void clear() {
        LexValue::clear();
        code.clear();
        line = column = 0;
    }
};

typedef std::shared_ptr<const LexToken> PLexToken;
