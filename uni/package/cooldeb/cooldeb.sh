# vim: set filetype=sh :

alias ver-on='_base=${PWD##*/}; cd ..; cd `dir-ver "$_base"`'
alias ver-off='_base=${PWD##*/}; cd ..; cd `dir-nover "$_base"`'
alias ver-refresh='ver-off; ver-on'

alias dh_makever='. dh_make-autover --createorig'

alias newaps='newdeb -aps'

alias dcon='vicontrol'

alias updist='svn-update-and-deb-redist'

alias depc='dpatch-edit-patch -c'

alias eachdeb='eachdir -rfdebian/control'

function cd-debian-package() {
    local dir
    local base
    while read dir; do
        base="${dir%/}"
        base="${base##*/}"
        if [ "$base" = "$1" ]; then
            cd "$dir"
            return 0
        fi
    done < <(eachdir -rlf debian/control)
    echo "No such package: $1" >&2
    return 1
}
alias TP='cd-debian-package'
