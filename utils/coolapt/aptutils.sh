alias search='postproc xxcat \; aptitude search'
alias show='postproc xxcat \; aptitude show'

alias safi='sudo apt-fast install --allow-unauthenticated'
alias sagi='sudo apt-get install --allow-unauthenticated'
alias sagu='sudo apt-get remove'
alias sagp='sudo apt-get purge'
alias sagr='sudo apt-get-reinstall2'
alias sagc='sudo apt-get autoclean'
alias sau='sudo apt-get update; sudo apt-get dist-upgrade --allow-unauthenticated'
alias dtree='LANG=C postproc 2tree -c \; postproc grep -v "^diverted by" \; dpkg -L'
