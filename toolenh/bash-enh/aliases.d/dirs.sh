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

alias  d="ls -oF -N --color --block-size=\"'1\""
alias d.='d -d'
alias dw='. d-which'
alias  l="ls -alF -N --color --block-size=\"'1\""

alias df='postproc sort \; df -T -B 1048576'

alias du1='du --max-depth=1'
alias du2='du --max-depth=2'

alias tree='tree-less'
