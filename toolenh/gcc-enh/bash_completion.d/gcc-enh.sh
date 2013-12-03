# vim: set filetype=sh :

_gcc_eval() {
    local prev=${COMP_WORDS[COMP_CWORD-1]}
    local cword=`_get_cword`
    local cwlen=${#cword}
    local cwtype=
    COMPREPLY=()

    case "$prev" in
        --expand)
            cwtype=macro;;
        -e)
            cwtype=macro;;
    esac

    case "$cwtype" in
        macro)
            COMPREPLY=($( echo | gcc -dM -E - \
                | cut -d\  -f2 | sed -e 's/(.*//' ))
            COMPREPLY=($( compgen -W "${COMPREPLY[*]}" -- "$cword" ))
            ;;
        *)
            opts=(-a --arg -d --dlang -x --lang -e --expand -f --format
                -q --quiet -v --verbose -h --help --version)
            COMPREPLY=($( compgen -o default -W "${opts[*]}" -- "$cword" ))
            ;;
    esac
} &&
complete -F _gcc_eval gcc-eval
