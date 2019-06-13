. shlib-import findabc

function findcont() {
    local d="$1" # root dir
    local n
    local cd

    shift
    if [ $# = 0 ] || [ "$1" = '-l' ]; then
        shift
        cooltask_list "$@"
        return
    fi

    if [ "$1" = '-C' ]; then cd=1; shift; fi
    if [ "$1" = '-A' ]; then
        shift
        local abbr="$1"
        local len=${#abbr} i
        local opts=()
        for (( i = 0; i < len; i++)); do
            opts=("${opts[@]}" ${abbr:i:1})
        done
        set -- "${opts[@]}"
    fi

    while [ $# -ge 2 ]; do
        if ! d=`findabc -ap "$1" "$d"`; then
            echo "Failed to find $1 (in $d)" >&2
            return 1
        fi
        shift
    done

    if [ "$cd" = 1 ]; then
        findabc -a "$1/" "$d"
    else
        findabc -ap "$1" "$d"
    fi
}

# Using `T <job-class> <job-id>` for `J` and `Jwhich`.
alias T='findcont %cooltask -C'
alias t='findcont %cooltask -C -A'
#alias J='findcont %cooljob -C'
alias T.='findcont %cooltask'
#alias Jwhich='findcont %cooljob'

alias Q='qlog'
