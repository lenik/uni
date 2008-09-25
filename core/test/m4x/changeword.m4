
m4_define(`hello', 'small')
m4_define(`$hello', 'big')

m4_changeword(`[_a-zA-Z0-9]+')
small=[hello]
big=[$hello]

m4_changeword(`$[_a-zA-Z0-9]+')
small=[hello]
big=[$hello]
