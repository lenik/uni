m4_include(`m4x/util.m4')
Start:

`m4x_echo test' =
    "m4x_echo()"
    "m4x_echo(arg1)"
    "m4x_echo(arg1, arg2)"
    "m4x_echo(arg1, arg2, arg3)"

`m4x_reverse test' =
    "m4x_reverse()"
    "m4x_reverse(arg1)"
    "m4x_reverse(arg1, arg2)"
    "m4x_reverse(arg1, arg2, arg3)"
    "m4x_reverse(0`'m4x_for(`i', 1, 10, `, i'))"

`dumpdef: m4x_reverse' =
    "m4_dumpdef(`m4x_reverse')"

End.