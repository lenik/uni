m4_include(`m4x/base.m4')

`dumpdef: m4x_cc' =
    "m4_dumpdef(`m4x_cc')"

`dumpdef: _m4x_cc' =
    "m4_dumpdef(`_m4x_cc')"

4X_BEGIN
    m4_define(`x', ``<x>'')
    m4_define(`y', ``<y>'')
    m4_define(`z', ``<z>'')
M4X_END

init:
X=x

`m4x_cc test' =
    "m4x_cc()"
    "m4x_cc(1)"
    "m4x_cc(1, 2)"
    "m4x_cc(1, 2, 3)"
    "m4x_cc(x, `y', z, t)"
