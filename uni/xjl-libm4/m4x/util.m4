m4_include(`m4x/base.m4')
M4X_BEGIN()

# M4 Utilities
#
# M4 Commono Utilities
#

m4_define(`m4x_echo', `$@')

m4_define(`m4x_reverse',
    `m4_ifelse($#, 0, , $#, 1, ``$1'', `m4x_reverse(m4_shift($@)), `$1'')')

M4X_END()