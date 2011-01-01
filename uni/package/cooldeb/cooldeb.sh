alias ver-on='_base=${PWD##*/}; cd ..; cd `dir-ver "$_base"`'
alias ver-off='_base=${PWD##*/}; cd ..; cd `dir-nover "$_base"`'
alias ver-refresh='ver-off; ver-on'

alias dh_makever='. dh_make-autover --createorig'

alias newaps='newdeb -aps'

alias dcon='vicontrol'

alias updist='svn-update-and-deb-redist'

alias epc='edit-patch -c'
alias depc='dpatch-edit-patch -c'

alias eachdeb='eachdir -rfdebian/control'
