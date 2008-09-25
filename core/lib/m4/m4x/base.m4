m4_pushdef(`M4X_DIVERT', m4_divnum)m4_dnl
m4_divert(-1)

# M4 Utilities
#
# M4 Language Extension
#

m4_ifdef(`m4x_once_m4_ext', , `
m4_define(`m4x_once_m4_ext', 1)

m4_define(`m4x_quote', ``$@'')
m4_define(`m4x_expand', `$*')

m4_changequote([, ])
m4_define([m4x_lq], [`])
m4_define([m4x_rq], ['])
m4_changequote(`, ')

m4_define(`m4x_nl', `
')

m4_define(`m4x_dump',
    `m4_ifelse($#, 0, , $#, 1, `[$1]',
        `[$1]'m4x_nl`m4x_dump(m4_shift($@))')')

# deprecated.
m4_define(`m4x_concat',
    `m4_ifelse($#, 0, , $#, 1, ``$1'',
        ``$1'm4x_concat(m4_shift($@))')')

# the arguments passed in is dequoted, so re-quote it again by ``$?''
m4_define(`m4x_cc',
    `m4_ifelse($#, 0, , $#, 1, ``$1'',
        ``$1'm4x_cc(m4_shift($@))')')

m4_define(`m4x_join',
    `m4_ifelse($#, 0, , $#, 1, , $#, 2, ``$2'',
        ``$2'`$1'm4x_join(`$1', m4_shift(m4_shift($@)))')')

m4_define(`m4x_stripnulls',
    `m4_ifelse($#, 0, , $#, 1, ``$1'',
        `m4_ifelse(`$1', `', `m4x_stripnulls(m4_shift($@))',
            `m4_ifelse(m4x_quote(m4x_stripnulls(m4_shift($@))), ``'', `$1',
                ``$1', m4x_stripnulls(m4_shift($@))')')')')

m4_define(`m4x_path_normalize',
    `m4_ifelse($#, 0, , $#, 1,
        `m4_ifelse(`$1', `', , `$1', ., , `$1/')',
        `m4_ifelse(`$1', `', , `$1', ., , `$1/')m4x_path_normalize(m4_shift($@))')')

# path fix: m4x_include(file, [dir])
m4_define(`M4X_CWD', `')
m4_define(`m4x_include', m4x_cc(
    `m4_pushdef(`M4X_CWD', m4x_quote(M4X_CWD, `$2'))',
    `m4_include(m4x_path_normalize(M4X_CWD)$1)',
    `m4_popdef(`M4X_CWD')'))

# diverting for space
m4_define(`M4X_BEGIN', m4x_cc(
    `m4_pushdef(`M4X_DIVERT', m4_divnum)',
    `m4_divert(-1)'))

m4_define(`M4X_END', m4x_cc(
    `m4_undivert(-1)',
    `m4_divert(M4X_DIVERT)',
    `m4_popdef(`M4X_DIVERT')m4_dnl'))

m4_define(`M4X___ORIGINAL_FILE__',
    `warning:  this file is automatically generated by m4 scripts.  please do not edit this file, you may lose all your changes in this file after the original file is re-built.  ')

# special definer
m4_define(`m4x_lazydef',
    `m4_ifdef(`$1', ,
        `m4_define(`$1', `$2')')')

# m4x_for(VAR, FROM, TO, BODY)
m4_define(`m4x_for', m4x_cc(
    `m4_pushdef(`$1', `$2')',
    `_m4x_for(`$1', `$2', `$3', `$4')',
    `m4_popdef(`$1')'))

m4_define(`_m4x_for',
    `$4`'m4_ifelse($1, `$3', , m4x_cc(
        `m4_define(`$1', m4_incr($1))',
        `_m4x_for(`$1', `$2', `$3', `$4')'))')

# m4x_forsplit(VAR, TEXT, DELIM, BODY)
m4_define(`m4x_forsplit', m4x_cc(
    `m4_pushdef(`$1')',
    `m4_pushdef(`_i_$1')',
    `_m4x_forsplit(`$1', `$2', `$3', `$4')',
    `m4_popdef(`_i_$1')',
    `m4_popdef(`$1')'))

# TODO: comma(,) isn't supported
m4_define(`_m4x_forsplit', m4x_cc(
    `m4_define(`_i_$1', m4_index(`$2', `$3'))',
    `m4_ifelse(_i_$1, -1, `m4_define(`$1', `$2')'`$4', m4x_cc(
        `m4_define(`$1', m4_substr(`$2', 0, _i_$1))',
        `$4',
        `_m4x_forsplit(`$1',
            m4_substr(`$2', m4_incr(_i_$1)),
            `$3', `$4')'))'))

# m4x_find(key, list...)
m4_define(`_m4x_find',
    `m4_ifelse($#, 0, , $#, 1, , $#, 2, ,
        $#, 3, `m4_ifelse(`$2', `$3', $1)',
        `m4_ifelse(`$2', `$3', $1,
            `_m4x_find(m4_incr($1), `$2', m4_shift(m4_shift(m4_shift($@))))')')')

m4_define(`m4x_find',
    `m4_ifelse($#, 0, , $#, 1, , `_m4x_find(0, $@)')')

# m4x_ifexist(key, `list...', exist, [not-exist<key, `list...', ...>])
m4_define(`m4x_ifexist',
    `m4_ifelse(m4x_find(`$1', $2), `',
        `m4_ifelse($#, 3, , $#, 4, `$4',
            `m4x_ifexist($4, `$5', m4_shift(m4_shift(m4_shift(m4_shift(m4_shift($@))))))')',
        `$3')')

')

m4_undivert(-1)m4_dnl
m4_divert(M4X_DIVERT)m4_dnl
m4_popdef(`M4X_DIVERT')m4_dnl