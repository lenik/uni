
alias sagi='sudo apt-get install --allow-unauthenticated'
alias sagr='sudo apt-get remove'
alias sagp='sudo apt-get purge'
alias sau='sudo apt-get update; sudo apt-get dist-upgrade --allow-unauthenticated'

alias ver-on='_base=${PWD##*/}; cd ..; cd `dir-ver "$_base"`'
alias ver-off='_base=${PWD##*/}; cd ..; cd `dir-nover "$_base"`'
alias ver-refresh='ver-off; ver-on'

alias dh_makever='. dh_make-autover --createorig'

alias newdeb='newdeb -aps'
