
alias sagi="sudo apt-get install"

alias ver-on='_base=${PWD##*/}; cd ..; cd `dir-ver "$_base"`'
alias ver-off='_base=${PWD##*/}; cd ..; cd `dir-nover "$_base"`'
alias ver-refresh='ver-off; ver-on'
