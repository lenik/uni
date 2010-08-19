m4_include(`m4x/base.m4')
M4X_BEGIN()

# M4 Import Environment Variables
#

# m4x_env([PREFIX])

m4_define(`_m4x_env',
    m4_translit(m4x_quote(m4_esyscmd(`env')), `,()`'', `???'))

# BUG: recursive cause dead
# BUG: prefix`' will cause the right-operand evaluated.
m4_define(`m4x_env', m4x_cc(
    `m4_pushdef(`prefix', m4_ifelse($#, 0, , ``$1''))',
    `m4x_forsplit(`line', _m4x_env, m4x_nl,
        m4x_cc(
            `m4_define(`eq', m4_index(line, `='))',
            `m4_ifelse(eq, -1, , m4x_cc(
                `m4_define(`nam', prefix`'m4_substr(line, 0, eq))',
                `m4_define(`val', m4_substr(line, m4_incr(eq)))',
                `m4_define(nam, val)'))'))',
    `m4_popdef(`prefix')'))'))

M4X_END()