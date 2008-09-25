m4_include(`m4x/env.m4')


_m4x_env
======================================
m4_index(_m4x_env, =)


-- with prefix:
m4x_env(`my_')
[result `USERNAME'=USERNAME]
[result `my_USERNAME'=my_USERNAME]


-- word:
m4x_env
[result `USERNAME'=USERNAME]
[result `my_USERNAME'=my_USERNAME]
