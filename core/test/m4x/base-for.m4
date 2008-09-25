m4_include(`m4x/base.m4')

`dumpdef: m4x_for' =
    "m4_dumpdef(`m4x_for')"

`dumpdef: _m4x_for' =
    "m4_dumpdef(`_m4x_for')"

`m4x_for test' =
    "m4x_for(`i', 1, 10, `(i)')"
    "m4x_for(`i', 1, 3,
        `m4x_for(`j', 1, 10, `(i, j)')m4x_nl')"
