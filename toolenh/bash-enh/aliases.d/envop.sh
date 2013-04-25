# vim: set filetype=sh :

# Display variables...
function dv() {
    local var
    for var in "$@"; do
        if [ "${var:0:1}" = '@' ]; then
            var="${var#@}"
            echo "$var=(array)"
            var="$var[@]"
            echoli "${!var}" | sed -e 's/^/    /'
        else
            echo "$var=${!var}"
        fi
    done
}

function paths() {
    local var=PATH
    local mesg

    if [ $# -gt 1 ]; then
        for var in "$@"; do
            mesg="Environ $var: "
            echo "$mesg"
            echo "${mesg//?/-}"
            var="${!var}"
            echo -e "${var//:/\\n}"
            echo
        done
    else
        if [ -n "$1" ]; then
            var=$1
        fi
        var="${!var}"
        echo -e "${var//:/\\n}"
    fi
}

function pathadd() {
    local a
    for a in "$@"; do
        case "$a" in
            .)
                a="$PWD";;
            ./*)
                a="$PWD/${a#./}";;
            *)
                a=`readlink -f "$a"`;;
        esac
        PATH="$PATH:$a"
    done
}
