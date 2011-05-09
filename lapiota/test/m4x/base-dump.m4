m4_include(`m4x/base.m4')

`dumpdef: m4x_dump' =
    "m4_dumpdef(`m4x_dump')"

`m4x_dump test' =
    "m4x_dump(a, b, c)"
    "m4x_dump(a,
        b,
        c
        )"
