alias pi='postproc xxcat \; aptitude show'      # package info
alias pif='postproc xxcat \; aptitude search'   # package info find

alias safi='sudo apt-fast install --allow-unauthenticated'
alias sagb='sudo apt-get build-dep --allow-unauthenticated'
alias sagi='sudo apt-get install --allow-unauthenticated'
alias sagu='sudo apt-get remove'
alias sagp='sudo apt-get purge'
alias sagr='sudo apt-get-reinstall2'
alias sagc='sudo apt-get clean; sudo apt-get autoclean; sudo apt-get autoremove'
alias sau='sudo apt-get update; sudo aptitude full-upgrade'
alias sadu='sudo apt-get update; sudo apt-get dist-upgrade'
alias dtree='LANG=C postproc 2tree -c \; postproc grep -v "^diverted by" \; dpkg -L'

alias acp='apt-cache policy'
alias sdi='sudo dpkg -i'
alias sdr='sudo dpkg-reconfigure'
