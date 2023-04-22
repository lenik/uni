#pragma once

#include "Element.h"

namespace arglang {

    class Argument;
    class Arguments;
    class CmdLine;
    class ControlLabel;
    class Element;
    class EvalStatement;
    class Expr;
    class FileSpec;
    class ForStatement;
    class FunctionCall;
    class Identifier;
    class IfStatement;
    class LiteralValue;
    class Parameter;
    class Parameters;
    class Program;
    class RedirectFragment;
    class RedirectFragments;
    class RedirectStatement;
    class Statement;
    class Statements;
    class SwitchCase;
    class SwitchCases;
    class SwitchStatement;
    class VarDeclaration;
    class VarDeclarations;
    class WhileStatement;

    typedef std::shared_ptr<Argument> PArgument;
    typedef std::shared_ptr<Arguments> PArguments;
    typedef std::shared_ptr<CmdLine> PCmdLine;
    typedef std::shared_ptr<ControlLabel> PControlLabel;
    typedef std::shared_ptr<Element> PElement;
    typedef std::shared_ptr<EvalStatement> PEvalStatement;
    typedef std::shared_ptr<Expr> PExpr;
    typedef std::shared_ptr<FileSpec> PFileSpec;
    typedef std::shared_ptr<ForStatement> PForStatement;
    typedef std::shared_ptr<FunctionCall> PFunctionCall;
    typedef std::shared_ptr<Identifier> PIdentifier;
    typedef std::shared_ptr<IfStatement> PIfStatement;
    typedef std::shared_ptr<LiteralValue> PLiteralValue;
    typedef std::shared_ptr<Parameter> PParameter;
    typedef std::shared_ptr<Parameters> PParameters;
    typedef std::shared_ptr<Program> PProgram;
    typedef std::shared_ptr<RedirectFragment> PRedirectFragment;
    typedef std::shared_ptr<RedirectFragments> PRedirectFragments;
    typedef std::shared_ptr<RedirectStatement> PRedirectStatement;
    typedef std::shared_ptr<Statement> PStatement;
    typedef std::shared_ptr<Statements> PStatements;
    typedef std::shared_ptr<SwitchCase> PSwitchCase;
    typedef std::shared_ptr<SwitchCases> PSwitchCases;
    typedef std::shared_ptr<SwitchStatement> PSwitchStatement;
    typedef std::shared_ptr<VarDeclaration> PVarDeclaration;
    typedef std::shared_ptr<VarDeclarations> PVarDeclarations;
    typedef std::shared_ptr<WhileStatement> PWhileStatement;

}
