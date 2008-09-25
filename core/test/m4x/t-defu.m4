m4_include(`m4x/base.m4')
m4_include(`m4x/base.m4')
M4X_BEGIN()
m4_define(`a', `good')
m4x_lazydef(`a', `bad-u')
m4x_lazydef(`b', `good-u')
m4x_lazydef(`b', `bad-u')
M4X_END()
`a': a
`b': b
