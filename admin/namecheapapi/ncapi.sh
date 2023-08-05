# -*- bash -*-
# vim: filetype=sh

_namecheapapi()
{
    local args=()
    local narg=0
    local arg
    local i

    #namecheapapi --completion "${COMP_WORDS[@]:1}" >&2
    
    COMPREPLY=($( namecheapapi --completion "${COMP_WORDS[@]:1}" ))
} &&
complete -F _namecheapapi namecheapapi ncapi
