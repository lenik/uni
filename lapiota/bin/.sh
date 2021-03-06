#!/bin/bash
    : ${RCSID:=$Id: - @VERSION@ @DATE@ @TIME@ - $}
    : ${PROGRAM_TITLE:="Bash_simple_cli_program_template"}
    : ${PROGRAM_SYNTAX:="[OPTIONS] [--] ..."}

    . shlib-import cliboot
    option -q --quiet       "Repeat to get less info"
    option -v --verbose     "Repeat to get more info"
    option -h --help        "Show this help page"
    option    --version     "Print the version info"

function setopt() {
    case "$1" in
        -h|--help)
            help; exit;;
        -q|--quiet)
            LOGLEVEL=$((LOGLEVEL - 1));;
        -v|--verbose)
            LOGLEVEL=$((LOGLEVEL + 1));;
        --version)
            version; exit;;
        *)
            quit "invalid option: $1";;
    esac
}

function main() {
    dump "$@"
    echo Done.
}

boot "$@"
