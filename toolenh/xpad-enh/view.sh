function view() {
    for f in "$@"; do
        full=`readlink -f "$f"`
        xpad -f "$full"
    done &
}
