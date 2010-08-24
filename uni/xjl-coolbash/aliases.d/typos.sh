
alias cd..='cd ..'
alias cd...='cd ../..'
alias cd....='cd ../../..'

function cdd() {
    cd "$1"
    shift
    if [ $# = 0 ]; then
        d
    else
        for a in "$@"; do
            d "$a"
        done
    fi
}
alias ccd=cdd
