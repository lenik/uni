#!/bin/bash
    : ${RCSID:=$Id: - @VERSION@ @DATE@ @TIME@ - $}
    : ${PACKAGE:=@PACKAGE@}
    : ${PROGRAM_TITLE:=<?= TEXT ?>}
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
