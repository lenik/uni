#!/bin/bash
    . shlib-import cliboot

    RCSID='$Id$'
    short_opts="hqv"
    long_opts="help,quiet,verbose,version"

function version() {
    parse_id "$RCSID"
    echo "[$BASENAME] @TEXT@"
    echo "Written by Lenik, Version 0.$rcs_rev, Last updated at $rcs_date"
}

function help() {
    version
    echo
    echo "Syntax: "
    echo "    $0 [OPTION] [--] ..."
    echo
    echo "Options: "
    echo "    -q, --quiet             Repeat to get less info"
    echo "    -v, --verbose           Repeat to get more info"
    echo "    -h, --help              Show this help page"
    echo "        --version           Print the version info"
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