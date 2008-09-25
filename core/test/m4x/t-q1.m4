m4_define(`foo', `m4_defined-FOO')m4_dnl
m4_define(`bar1', BAR1-with-foo-inline)m4_dnl
m4_define(`bar2', `BAR2-with-foo-inline')m4_dnl
m4_define(`bar3', BAR3-with-`foo'-inline)m4_dnl
m4_define(`bar4', ``BAR4-with-foo-inline'')m4_dnl
`bar1' = bar1
`bar2' = bar2
`bar3' = bar3
`bar4' = bar4
