# vim: set filetype=sh :

function main() {
    tmp=`tempfile`

    for f in "$@"; do
        _log2 "Decode $f => $tmp"
        yangwei -d "$f" | tee $tmp.bak >$tmp

        _log2 "Execute $EDITOR $tmp"
        $EDITOR "$tmp" || quit "Editor error or user abort."

        if diff -q $tmp $tmp.bak; then
            _log2 "No change."
        else
            _log2 "Encode $tmp => $f"
            yangwei -e $tmp >"$f"
        fi
    done

    rm -f "$tmp"
    rm -f "$tmp.bak"
}
