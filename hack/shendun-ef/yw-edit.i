function main() {
    tmp=`tempfile`

    for f in "$@"; do
        _log2 "Decode $f => $tmp"
        yangwei -d "$f" >$tmp

        _log2 "Execute $EDITOR $tmp"
        $EDITOR "$tmp" || quit "Editor error or user abort."

        _log2 "Encode $tmp => $f"
        yangwei -e $tmp >"$f"
    done

    rm -f "$tmp"
}
