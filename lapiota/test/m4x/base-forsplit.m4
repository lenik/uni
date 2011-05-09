m4_include(`m4x/base.m4')

`dumpdef: m4x_forsplit' =
    "m4_dumpdef(`m4x_forsplit')"

`dumpdef: _m4x_forsplit' =
    "m4_dumpdef(`_m4x_forsplit')"

`m4x_forsplit test' =
    "m4x_forsplit(`part', `aaa.bb.ccc', `.', `(part)')"
    "m4x_forsplit(`part', `aaa.bb.ccc...eee..', `.', `(part)')"
    "m4x_forsplit(`part', `a.b,c.d,e', `.', `(part)')"
