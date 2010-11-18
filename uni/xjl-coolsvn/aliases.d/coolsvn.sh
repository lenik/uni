alias grep-svn='grep --exclude-dir .svn'

alias unadd='svn-unadd'
alias    ci='svn-commit-autover'
alias    di='svn di'
alias    st='svn st'
alias   log='svn-log'

alias    up='svn update'
alias  upim='svn update --set-depth immediates'
alias  upin='svn update --set-depth infinity'

alias  sraw='svn resolve --accept working'
alias upraw='svn update; svn resolve --accept working'
