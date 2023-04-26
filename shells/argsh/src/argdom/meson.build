sources = [
    'Argument.cc',
    'Arguments.cc',
    'BinaryExpr.cc',
    'CForStatement.cc',
    'CmdLine.cc',
    'ControlLabel.cc',
    'Element.cc',
    'EvalStatement.cc',
    'Expr.cc',
    'FileSpec.cc',
    'ForEachStatement.cc',
    'ForStatement.cc',
    'FunctionCall.cc',
    'Identifier.cc',
    'IfStatement.cc',
    'LiteralValue.cc',
    'NopStatement.cc',
    'Parameter.cc',
    'Parameters.cc',
    'Program.cc',
    'RawChars.cc',
    'RedirectFragment.cc',
    'RedirectFragments.cc',
    'RedirectStatement.cc',
    'SimpleStatement.cc',
    'Statement.cc',
    'Statements.cc',
    'SwitchCase.cc',
    'SwitchCases.cc',
    'SwitchStatement.cc',
    'VarDeclaration.cc',
    'VarDeclarations.cc',
    'VarRef.cc',
    'WhileStatement.cc',
    ]

libdom = static_library('argdom', 
    sources,
    dependencies: [ dep_incl ],
    install: true
    )

headers = []
foreach f: sources
    headers += f.replace('.cxx', '.hxx')
endforeach

install_headers('../argdom.h')
install_headers(headers,
    subdir: 'argdom'
)