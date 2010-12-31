. shlib-import findabc

[ -z "$COOLTASK_HOME" ] && COOLTASK_HOME=~/tasks

export COOLTASK_HOME

function cooltask() {
    local d="$COOLTASK_HOME"
    local n
    local cd

    if [ $# = 0 ] || [ "$1" = '-l' ]; then
        shift
        cooltask_list "$@"
        return
    fi

    if [ "$1" = '-C' ]; then
        cd=1
        shift
    fi

    while [ $# -ge 2 ]; do
        if ! d=`findabc -p "$1" "$d"`; then
            echo "Failed to find $1 (in $d)" >&2
            return 1
        fi
        shift
    done

    if [ "$cd" = 1 ]; then
        findabc "$1/" "$d"
    else
        findabc -p "$1" "$d"
    fi
}

alias T='cooltask'
alias TO='cooltask -C'
