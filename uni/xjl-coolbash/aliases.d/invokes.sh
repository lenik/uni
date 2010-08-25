
alias @='gnome-open'

function orig() {
    local cmd
    if [ $# = 0 ]; then
        echo orig COMMAND ARGUMENTS...
        return 1
    fi
    if ! cmd=`which "$1"`; then
        echo "Bad command: $1"
        return 1
    fi
    shift
    "$cmd" "$@"
}
