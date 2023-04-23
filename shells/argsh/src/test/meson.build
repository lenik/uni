
executable('lexer-test',
    [ 'lexer-test.cc' ],
    include_directories: [
        include_directories('../parser')
    ],
    link_with: [ liblexer ],
    dependencies: dep_incl,
    install: true
    )

executable('parser-test',
    [ 'parser-test.cc' ],
    include_directories: [
        include_directories('../parser')
    ],
    link_with: [ liblexer, libparser ],
    dependencies: dep_incl,
    install: true
    )
