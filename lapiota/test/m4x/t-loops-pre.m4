
m4_define(add_pre, Prefix_$1)dnl
See the prefix: add_pre(hello)

m4_define(make_pre,
    `m4_ifelse($#, 0, NONE!, $#, 1, `add_pre(`$1')',
            `add_pre(`$1'), make_pre(m4_shift($@))')')dnl
See the prefixes: make_pre(arg1, arg2, arg3)

What happened if no arguments?
See the 0-prefixes: make_pre


_make_pre(...) =>
# ifelse($#, 0, , $#, 1, `add_pre(`$1')',
#       `add_pre(`$1'), make_pre(shift($@))')

_make_pre(a, b, c) =>
# ifelse(3, 0, , 3, 1, `add_pre(`$1')',
#       `add_pre(`$1'), make_pre(shift($@))')       | a, b, c
# =>
# add_pre(`$1'), make_pre(shift($@))                | a, b, c
# =>
# Prefix_$1, make_pre(b, c)                         | a, b, c
# =>
# Prefix_a, ifelse(2, 0, , 2, 1, `add_pre(`$1')',
#               `add_pre(`$1'), make_pre(shift($@))')
# ...
