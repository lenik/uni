#!/bin/bash
    . shlib-import cliboot

    RCSID='$Id: - @VERSION@ @DATE@ @TIME@ - $'

    option -q --quiet       "Repeat to get less info"
    option -v --verbose     "Repeat to get more info"
    option -h --help        "Show this help page"
    option     --version    "Print the version info"

function version() {
    parse_id "$RCSID"
    echo "[$BASENAME] <?= TEXT ?>"
    echo "Written by Lenik, Version $rcs_rev, Last updated at $rcs_date"
}

function help() {
    version
    echo
    echo "Syntax: "
    echo "    $0 [OPTION] [--] ..."
    echo
    help_options
}

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
