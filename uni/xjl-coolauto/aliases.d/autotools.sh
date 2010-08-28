alias reconf='autoreconf2 -is'
alias unconf='autounconf'

alias reinst='reconf && makelook && sudo make install && unconf'
