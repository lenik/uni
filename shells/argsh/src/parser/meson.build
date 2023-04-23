
reflex = find_program('reflex', required: true)
bison = find_program('bison', required: true)

docdir = custom_target(
    'mkdir-doc',
    output: 'doc',
    command: [ 'mkdir', '-p', 'doc' ]
    )

lexergen = generator(
    reflex,
    output: [ '@BASENAME@.cc', '@BASENAME@.h' ],
    arguments: [
        '--c++',
        '--bison-cc',
        '--unicode',
        '--header-file=@OUTPUT1@',
        '--graphs-file=doc/@BASENAME@.dot',
        '-o', '@OUTPUT0@', 
        '@INPUT@'
        ],
    depends: docdir
    )

parsergen = generator(
    bison,
    output: [
        '@BASENAME@.cc', 
        '@BASENAME@.h'
        ],
    arguments: [
        '-Lc++',
        '--header=@OUTPUT1@',
		'--graph=doc/@BASENAME@.dot',
		'--html=doc/@BASENAME@.html',
		'--xml=doc/@BASENAME@.xml',
		'--token-table',
		'--feature=caret,fixit',
		'-Wcounterexamples',
		'-o', '@OUTPUT0@',
        '@INPUT@'
        ],
    depends: docdir
    )

arglang_lexer = lexergen.process('arglang-lexer.lex')
arglang_parser = parsergen.process('arglang-parser.yacc')

cpp = meson.get_compiler('cpp')
libreflex = cpp.find_library('reflex', required: true)

liblexer = static_library('lexer',
    [
        arglang_lexer
    ],
    link_with: [ libdom ],
    dependencies: [ dep_incl, libreflex ]
    )

libparser = static_library('parser',
    [
        arglang_parser
    ],
    link_with: libdom,
    dependencies: dep_incl
    )
