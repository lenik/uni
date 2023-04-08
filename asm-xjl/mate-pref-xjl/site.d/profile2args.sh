#!/bin/bash
    : ${RCSID:=$Id: - @VERSION@ @DATE@ @TIME@ - $}
    : ${PACKAGE:=@PACKAGE@}
    : ${PROGRAM_TITLE:=}
    : ${PROGRAM_SYNTAX:=[OPTIONS] [--] ...}

    . shlib-import cliboot
    option -q --quiet
    option -v --verbose
    option -h --help
    option    --version

function setopt() {
    case "$1" in
        -h|--help)
            help $1; exit;;
        -q|--quiet)
            LOGLEVEL=$((LOGLEVEL - 1));;
        -v|--verbose)
            LOGLEVEL=$((LOGLEVEL + 1));;
        --version)
            show_version; exit;;
        *)
            quit "invalid option: $1";;
    esac
}

function main() {
    local k v
    
    echo "# color mapping {{{"
    while IFS== read k v; do
        if [ -n "$v" ]; then
            if [ "${v:0:1}" = "'" ]; then
                n=${#v}
                v=${v:0:n - 1}
                v=${v:1}
            fi
        fi
        # echo "after trim $k:=$v."
        
        case "$k" in
            foreground-color)
                print_entry "foreground" $(color_conv "$v");;
            background-color)
                print_entry "background" $(color_conv "$v");;
            palette)
                pal=()
                for c in ${v//:/ }; do
                    c6=$(color_conv "${c##}")
                    pal+=("$c6")
                done
                for ((i = 0; i < 8; i++)); do
                    name=${COLOR_NAMES[i]}
                    light=$((i + 8))
                    echo "# color $name:"
                    print_entry "color$i" "${pal[i]}"
                    print_entry "color$light" "${pal[light]}"
                done
                ;;
        esac
    done
    echo "# }}}"
}

function print_entry() {
    local k="$1"
    local v="$2"
    printf "    %-12s%s\n" "$k" "$v"
}

COLOR_NAMES=(black red green yellow blue magenta cyan white)

function color_conv() {
    local arg
    for arg in "$@"; do
        # echo "arg:$arg" >&2
        local c="$arg"
        local r="${c:1:2}"
        local g="${c:5:2}"
        local b="${c:9:2}"
        echo "#$r$g$b"
    done
}

boot "$@"
