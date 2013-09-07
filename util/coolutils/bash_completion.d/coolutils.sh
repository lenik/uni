
function _htmldoc_man() {
    local cur="${COMP_WORDS[COMP_CWORD]}"
    local cache=/var/tmp/htmldoc-man.cache

    if [ $COMP_CWORD = 1 ]; then
        make1 -qoT 1wk $cache-pkg \; htmldoc-man -p
        COMPREPLY=( $( grep "^$cur" $cache-pkg ) )
    else
        local pkg="${COMP_WORDS[1]}"
        make1 -qoT 1wk "$cache.$pkg" \; htmldoc-man -al "$pkg"
        COMPREPLY=( $( grep "^$cur" "$cache.$pkg" ) )
    fi
    return 0
} && complete -F _htmldoc_man htmldoc-man hm
