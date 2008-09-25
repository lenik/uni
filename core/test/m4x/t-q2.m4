m4_define(`REVERSE',
    `m4_ifelse($#, 0, , $#, 1, $1, N)')m4_dnl
m4_dumpdef(`REVERSE')

m4_define(`ECHO', `$@')m4_dnl

ECHO(hello, world!, 4)
