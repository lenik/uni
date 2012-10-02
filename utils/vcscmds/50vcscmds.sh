# vim: set filetype=sh :

alias   add='vcscmd add'
alias   ann='vcscmd annotate'
alias blame='vcscmd blame'
alias    br='vcscmd branch'
alias    ci='vcscmd commit'
alias   ci+='vcscmd commit+'
alias   cig='vcscmd commit-gui'
alias clone='vcscmd clone'
alias    co='vcscmd checkout'
alias  cobr='vcscmd checkout-branch'
alias    di='vcscmd diff'
alias    ig='vcscmd ignore'
alias   log='vcscmd log'
alias    lg='vcscmd log-graph'
alias   lgg='vcscmd log-graph --name-status'
alias  pull='vcscmd pull'
alias  push='vcscmd push'
alias   rst='vcscmd restore'
alias   sho='vcscmd show'
alias    st='vcscmd status'
alias   tag='vcscmd tag'
alias unadd='vcscmd unadd'
alias    up='vcscmd update'
alias merge='vcscmd merge'
alias   exp='vcscmd export'

# Seems git-only.
alias bisect='vcscmd bisect'
alias rebase='vcscmd rebase'
alias remote='vcscmd remote'
alias  stash='vcscmd stash'

function vcscd() {
    local vcsroot=`vcscmd rootpath .`
    cd "$vcsroot/$1"
}

alias vcd='vcscd'
