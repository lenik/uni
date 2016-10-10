
function cwdex-cd() {
    local type=$1
    shift

    local arg
    local cdopts=()
    local hasdir=0
    local args=

    for arg in "$@"; do
        case "$arg" in
            -*)
                cdopts=("${cdopts[@]}" "$arg")
                ;;
            *)
                if [ "$hasdir" = 0 ]; then
                    cdopts=("${cdopts[@]}" "$arg")
                    hasdir=1
                else
                    args=("${args[@]}" "$arg")
                fi
                ;;
        esac
    done

    local olddir="$PWD"
    builtin $type "${cdopts[@]}" || return
    local newdir="$PWD"

    if [ -f "$olddir/.cwdex/leave" ]; then
        # leave() never fails.
        cd "$olddir"
        . ".cwdex/leave"
        cd "$newdir"
    fi

    if [ -f ".cwdex/enter" ]; then
        if ! . ".cwdex/enter"; then
            echo "Failed to enter, return back to $olddir." >&2
            case $type in
                cd)
                    builtin cd "$olddir";;
                pushd)
                    builtin popd;;
                popd)
                    builtin pushd "$olddir";;
            esac
            return 3
        fi
    fi

    return 0
}

alias __cd='builtin cd'
alias __pushd='builtin pushd'
alias __popd='builtin popd'

alias cd='cwdex-cd cd'
alias chdir='cwdex-cd cd'
alias pushd='cwdex-cd pushd'
alias popd='cwdex-cd popd'
