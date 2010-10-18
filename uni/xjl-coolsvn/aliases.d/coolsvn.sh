
alias svn=coolsvn

alias grep-svn='grep --exclude-dir .svn'

alias st='svn st'
alias ci='svn ci'
alias di='svn di --diff-cmd meld'

alias sraw='svn resolve --accept working'
alias upraw='svn update; svn resolve --accept working'

alias up='svn update'
alias upim='svn update --set-depth immediates'
alias upin='svn update --set-depth infinity'
