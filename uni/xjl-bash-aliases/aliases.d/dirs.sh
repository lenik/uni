alias cd.='cd `readlink -f .`'
alias cd..='. cd..n'
alias cd...='cd ../..'
alias cd....='cd ../../..'
alias cdd='. cd-ls'
alias ccd='. cd-ls'
alias cdf='. cd-first'
alias cd-alias='. cd-alias'

alias md='mkdir'
alias rd='rmdir'

alias d='ls  -oF  -N --color'
alias dd='d -d'
alias dt='d -t | tail'
alias dw='. ls-which'
alias l='ls  -aoF -N --color'
alias ll='ls -alF -N --color'

alias df='postproc sort \; df -hP'

alias du1='du --max-depth=1'
alias du2='du --max-depth=2'
